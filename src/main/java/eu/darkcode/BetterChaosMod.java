package eu.darkcode;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.networking.ClientboundPollStartPayload;
import eu.darkcode.networking.ClientboundPollUpdateVotesPayload;
import eu.darkcode.networking.Payloads;
import eu.darkcode.networking.configuration.task.HandshakeTask;
import eu.darkcode.networking.configuration.task.IntegrationCheckTask;
import eu.darkcode.server.EventManager;
import eu.darkcode.server.Poll;
import eu.darkcode.utils.Utils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static eu.darkcode.utils.Utils.MOD_ID;

public class BetterChaosMod implements ModInitializer {
    private static final @NotNull String CONFIG_FILE = "./config/betterchaosmod/config.json";
    private static final Permission.HasCommandLevel PERMISSION = new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS);
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static BetterChaosMod instance;
    private @Nullable EventManager eventManager;
    private Settings settings = new Settings();

    public static BetterChaosMod instance() {
        return instance;
    }

    public EventManager eventManager() {
        return eventManager;
    }

    @NotNull
    public Settings settings() {
        return settings;
    }

    @Override
    public void onInitialize() {
        instance = this;
        LOGGER.info("[Chaos-Server] EventRegistry initialized (events: {})", EventRegistry.EVENTS.size());
        loadSettings();
        LOGGER.info("[Chaos-Server] Settings loaded!");

        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            if (ServerConfigurationNetworking.canSend(handler, Payloads.S2C_HANDSHAKE)) {
                handler.addTask(new HandshakeTask(settings));
                handler.addTask(new IntegrationCheckTask());
                return;
            }
            handler.disconnect(Component.literal("Please download BetterChaosMod! https://bcm.matejtomecek.eu/"));
        });

        ServerConfigurationNetworking.registerGlobalReceiver(Payloads.C2S_HANDSHAKE_ACK, (payload, context) -> {
            if (!payload.clientModVersion().equals(UpdateStatus.getModVersion())) {
                context.responseSender().disconnect(Component.literal("Mod version mismatch! (Client: " + payload.clientModVersion() + "; Server: " + UpdateStatus.getModVersion() + ")"));
                return;
            }
            context.packetListener().completeTask(HandshakeTask.TYPE);
        });

        ServerConfigurationNetworking.registerGlobalReceiver(Payloads.C2S_INTEGRATION_PASS, (payload, context) -> {
            //TODO: Store integrations and show ingame? payload.integrations()
            context.packetListener().completeTask(IntegrationCheckTask.TYPE);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (eventManager == null)
                eventManager = new EventManager(server);
            // [RESEND STATUSES]
            Poll poll = eventManager.votingSystem().poll();
            sender.sendPacket(new ClientboundPollStartPayload(poll.definition()));
            sender.sendPacket(new ClientboundPollUpdateVotesPayload(poll.id(), poll.totalVotes(), poll.votes()));
        });

        ServerPlayNetworking.registerGlobalReceiver(Payloads.C2S_SEND_VOTES, (payload, context) -> {
            if (eventManager == null)
                return;
            eventManager.votingSystem().receiveVotes(payload.pollId(), payload.votes());
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (eventManager == null)
                return;
            eventManager.endPlayer(handler.player);
            if (PlayerLookup.all(server).size() <= 1) {
                eventManager.end();
                eventManager = null;
            }
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (eventManager != null)
                eventManager.tick();
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("betterchaosmod");
            LiteralArgumentBuilder<CommandSourceStack> execute = Commands.literal("execute")
                    .requires(source -> source.permissions().hasPermission(PERMISSION))
                    .then(Commands.argument("event", ResourceArgument.resource(registryAccess, EventRegistry.REGISTRY_KEY))
                            .executes(context -> {
                                Holder.Reference<EventRegistry.Entry<?>> event = ResourceArgument.getResource(context, "event", EventRegistry.REGISTRY_KEY);

                                if (eventManager != null)
                                    eventManager.executeEvent(event.value().construct());

                                return 1;
                            }));

            LiteralArgumentBuilder<CommandSourceStack> pause = Commands.literal("pause")
                    .requires(source -> source.permissions().hasPermission(PERMISSION))
                    .executes(context -> {
                        if (eventManager != null)
                            eventManager.setPaused(!eventManager.isPaused());

                        return 1;
                    });

            root.then(pause);

            dispatcher.register(root.then(execute));
        });
    }

    public void loadSettings() {
        try {
            this.settings = Utils.loadObject(CONFIG_FILE, Settings.class);
            if (this.settings == null) {
                this.settings = new Settings();
                LOGGER.info("[Chaos-Server] Creating default settings...");
                saveSettings();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load settings!", e);
        }
    }

    public void saveSettings() {
        try {
            Utils.saveObject(CONFIG_FILE, this.settings);
            LOGGER.info("[Chaos-Server] Settings saved!");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save settings!", e);
        }
    }
}
package eu.darkcode;

import eu.darkcode.event.EventRegistry;
import eu.darkcode.networking.ClientboundPollStartPayload;
import eu.darkcode.networking.Payloads;
import eu.darkcode.networking.configuration.task.HandshakeTask;
import eu.darkcode.networking.configuration.task.IntegrationCheckTask;
import eu.darkcode.server.EventManager;
import eu.darkcode.utils.Utils;
import eu.darkcode.voting.IntegrationType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static eu.darkcode.utils.Utils.MOD_ID;

public class BetterChaosMod implements ModInitializer {
    private static final @NotNull String CONFIG_FILE = "./config/betterchaosmod/config.json";
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
            handler.disconnect(Component.literal("Please download BetterChaosMod! https://store.matejtomecek.eu/betterchaosmod/"));
        });

        ServerConfigurationNetworking.registerGlobalReceiver(Payloads.C2S_HANDSHAKE_ACK, (payload, context) -> {
            System.out.println(">> " + payload.getClass().getName());

            if (!payload.clientModVersion().equals(UpdateStatus.getModVersion())) {
                context.responseSender().disconnect(Component.literal("Mod version mismatch! (Client: " + payload.clientModVersion() + "; Server: " + UpdateStatus.getModVersion() + ")"));
                return;
            }
            context.networkHandler().completeTask(HandshakeTask.TYPE);
        });

        ServerConfigurationNetworking.registerGlobalReceiver(Payloads.C2S_INTEGRATION_PASS, (payload, context) -> {
            System.out.println(">> " + payload.getClass().getName());

            System.out.println(context.responseSender().getClass().getName());
            for (IntegrationType integration : payload.integrations()) {
                System.out.println("Found integration: " + integration);
            }
            context.networkHandler().completeTask(IntegrationCheckTask.TYPE);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (eventManager == null)
                eventManager = new EventManager(server);
            // [RESEND STATUSES]
            sender.sendPacket(new ClientboundPollStartPayload(eventManager.votingSystem().poll()));
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
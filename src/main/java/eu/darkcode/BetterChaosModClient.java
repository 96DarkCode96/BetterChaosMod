package eu.darkcode;

import eu.darkcode.client.ClientSettings;
import eu.darkcode.client.EventManager;
import eu.darkcode.client.hud.CurrentPollPanel;
import eu.darkcode.client.hud.HudManager;
import eu.darkcode.client.overlay.OverlaySettings;
import eu.darkcode.client.overlay.OverlayWebServer;
import eu.darkcode.client.screen.GameLogScreen;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.networking.Payloads;
import eu.darkcode.networking.configuration.ServerboundHandshakeAck;
import eu.darkcode.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static eu.darkcode.utils.Utils.MOD_ID;

public class BetterChaosModClient implements ClientModInitializer {
    private static final @NotNull String CONFIG_FILE = "./config/betterchaosmod/client_config.json";
    private static final KeyMapping.Category KEYBIND_CATEGORY = KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath("betterchaosmod", "main"));
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static KeyMapping KEYBIND_OPEN_GAME_LOG;
    private static BetterChaosModClient instance;

    public @NotNull UpdateStatus updateStatus = UpdateStatus.UNKNOWN;
    private ClientSettings settings = new ClientSettings();
    private HudManager hudManager;
    private EventManager eventManager;

    public static BetterChaosModClient instance() {
        return instance;
    }

    public EventManager eventManager() {
        return eventManager;
    }

    @NotNull
    public ClientSettings settings() {
        return settings;
    }

    @NotNull
    public HudManager hudManager() {
        return hudManager;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        LOGGER.info("[Chaos-Client] EventRegistry initialized (events: {})", EventRegistry.EVENTS.size());

        loadSettings();

        settings.lastShownChangelog = null;

        hudManager = new HudManager();
        updateStatus = UpdateStatus.checkVersion();

        KEYBIND_OPEN_GAME_LOG = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.gamelog.open", GLFW.GLFW_KEY_U, KEYBIND_CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (KEYBIND_OPEN_GAME_LOG.consumeClick())
                client.setScreen(new GameLogScreen());
        });

        ClientConfigurationNetworking.registerGlobalReceiver(Payloads.S2C_HANDSHAKE, (payload, context) -> {
            System.out.println("<< " + payload.getClass().getName());

            context.responseSender().sendPacket(new ServerboundHandshakeAck(UpdateStatus.getModVersion()));
            eventManager = new EventManager(payload.serverSettings());
        });

        ClientConfigurationNetworking.registerGlobalReceiver(Payloads.S2C_INTEGRATION_CHECK, (payload, context) -> {
            System.out.println("<< " + payload.getClass().getName());

            if (eventManager == null) {
                context.responseSender().disconnect(Component.literal("Event manager is not initialized!"));
                return;
            }

            context.client().execute(() -> eventManager.setupIntegration(context));
        });

        registerPlayNetworking();

        OverlaySettings overlay = settings.overlay;
        if (overlay.enabled)
            OverlayWebServer.startServer(overlay.webServerPort);
    }

    private void registerPlayNetworking() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (eventManager != null)
                eventManager.end();
            eventManager = null;
        });

        ClientPlayNetworking.registerGlobalReceiver(Payloads.S2C_TICK, (payload, context) -> {
            if (eventManager == null)
                return;
            context.client().execute(() -> eventManager.tick(payload.timerCount()));
        });

        ClientPlayNetworking.registerGlobalReceiver(Payloads.S2C_POLL_START, (payload, context) -> {
            if (eventManager == null)
                return;
            context.client().execute(() -> {
                eventManager.votingSystem().processNewPoll(payload.poll());
                hudManager.hud(CurrentPollPanel.class).newPoll(payload.poll());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(Payloads.S2C_EVENT_START, (payload, context) -> {
            if (eventManager == null)
                return;
            context.client().execute(() -> eventManager.startEvent(payload.event()));
        });
    }

    public void loadSettings() {
        try {
            this.settings = Utils.loadObject(CONFIG_FILE, ClientSettings.class);
            if (this.settings == null) {
                this.settings = new ClientSettings();
                LOGGER.info("[Chaos-Client] Creating default settings...");
                saveSettings();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load settings!", e);
        }
    }

    public void saveSettings() {
        try {
            Utils.saveObject(CONFIG_FILE, this.settings);
            LOGGER.info("[Chaos-Client] Settings saved!");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save settings!", e);
        }
    }
}
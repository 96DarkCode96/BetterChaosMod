package eu.darkcode;

import eu.darkcode.event.EventRegistry;
import eu.darkcode.server.EventManager;
import eu.darkcode.utils.Utils;
import net.fabricmc.api.ModInitializer;
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
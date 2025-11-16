package eu.darkcode;

import eu.darkcode.event.EventRegistry;
import net.fabricmc.api.ClientModInitializer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static eu.darkcode.utils.Utils.MOD_ID;

public class BetterChaosModClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static BetterChaosModClient instance;
    public @NotNull UpdateStatus updateStatus = UpdateStatus.UNKNOWN;

    public static BetterChaosModClient instance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        LOGGER.info("[Chaos-Client] EventRegistry initialized (events: {})", EventRegistry.EVENTS.size());
        updateStatus = UpdateStatus.checkVersion();
    }
}
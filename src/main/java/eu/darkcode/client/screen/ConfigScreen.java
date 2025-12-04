package eu.darkcode.client.screen;

import eu.darkcode.BetterChaosMod;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.Settings;
import eu.darkcode.client.ClientSettings;
import eu.darkcode.client.hud.HudSettings;
import eu.darkcode.client.voting.integration.twitch.TwitchIntegrationSettings;
import eu.darkcode.client.widgets.config.OreInputOption;
import eu.darkcode.client.widgets.config.OreSidebarOption;
import eu.darkcode.client.widgets.container.OrePanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static eu.darkcode.client.widgets.impl.OreSlider.*;
import static eu.darkcode.client.widgets.utils.Ore.PASSWORD_FORMATTER;

public class ConfigScreen extends BaseScreen {
    private final @NotNull Settings settings;
    private final @NotNull ClientSettings clientSettings;
    private @NotNull Category category = Category.SERVER;
    private OrePanel panel;

    public ConfigScreen(OptionsScreen parent) {
        super(parent, Component.translatable("betterchaosmod.config.title"));
        settings = BetterChaosMod.instance().settings();
        clientSettings = BetterChaosModClient.instance().settings();
    }

    @Override
    public void onClose() {
        // [SAVE SERVER SETTINGS]
        BetterChaosMod.instance().saveSettings();
        // [SAVE CLIENT SETTINGS]
        BetterChaosModClient.instance().saveSettings();
        super.onClose();
    }

    @Override
    protected void init() {
        OrePanel sidebar = new OrePanel(1, 7, 1, 7, 0);
        for (Category value : Category.values()) {
            OreSidebarOption option = sidebar.addSidebarOption(value.key(), value.sprite(), category == value, () -> {
                category = value;
                this.clearWidgets();
                this.clearFocus();
                this.init();
            });
            if (category == value)
                setInitialFocus(option);
        }

        panel = new OrePanel();

        switch (category) {
            case Category.SERVER -> serverConfig();
            case Category.CLIENT -> clientConfig();
            default -> integrationConfig();
        }

        this.layout.addBackButton();
        this.layout.addToContents(sidebar);
        this.layout.addToContents(panel);
        super.init();
    }

    private void serverConfig() {
        boolean enabledOptions = Minecraft.getInstance().level == null;
        panel.addSlider(
                "server.duration.event",
                "server.duration.event_desc",
                settings.eventDuration / 20, 10, 120,
                FORMATTER_SECONDS,
                a -> settings.eventDuration = a * 20,
                enabledOptions
        );
        panel.addSlider(
                "server.duration.timer",
                "server.duration.timer_desc",
                settings.timerDuration / 20, 15, 300,
                FORMATTER_SECONDS,
                a -> settings.timerDuration = a * 20,
                enabledOptions
        );
    }

    private void clientConfig() {
        final HudSettings hud = clientSettings.hud;
        panel.addCategory("client.timer", null);
        panel.addSlider(
                "client.timer.height",
                null,
                hud.timerHeight(), 5, 40,
                FORMATTER_PIXELS,
                hud::timerHeight,
                true
        );
        panel.addColorOption("client.timer.foreground", null, hud.timerForegroundColor(), hud::timerForegroundColor, true);
        panel.addColorOption("client.timer.background", null, hud.timerBackgroundColor(), hud::timerBackgroundColor, true);
        panel.addCategory("client.poll", null);
        panel.addSlider(
                "client.poll.width",
                null,
                hud.pollWidth(), 150, 400,
                FORMATTER_PIXELS,
                hud::pollWidth,
                true
        );
        panel.addSlider(
                "client.activeEvents.maxEntries",
                null,
                hud.activeEventsMaxEntries(), 4, 12,
                FORMATTER_PLAIN,
                hud::activeEventsMaxEntries,
                true
        );
    }

    private void integrationConfig() {
        panel.addCategory("integration.twitch", "integration.twitch.description");

        boolean enabledOptions = Minecraft.getInstance().level == null;
        TwitchIntegrationSettings twitchIntegration = clientSettings.integration.twitchIntegration;

        OreInputOption channelName = panel.addTextInput("client.integration.twitch.channel", null, twitchIntegration.channel, a -> twitchIntegration.channel = a, enabledOptions);
        OreInputOption channelToken = panel.addTextInput("client.integration.twitch.token", null, twitchIntegration.token, a -> twitchIntegration.token = a, enabledOptions);
        channelToken.textInput().addFormatter(PASSWORD_FORMATTER);

        panel.addCategory("integration.overlay", "integration.overlay.description");
        //panel.addCheckbox("client.overlay.enabled", null, clientSettings.overlay.enabled, a -> clientSettings.overlay.enabled = a, true);
        OreInputOption webPort = panel.addTextInput("client.overlay.webPort", null, String.valueOf(clientSettings.overlay.webServerPort), a -> {
            try {
                int port = Integer.parseInt(a);
                if (port >= 1024 && port <= 65535)
                    clientSettings.overlay.webServerPort = port;
            } catch (NumberFormatException ignored) {
            }
        }, true);
        webPort.textInput().setFilter(s -> {
            if (s.isEmpty())
                return true;
            if (s.startsWith("0"))
                return false;
            try {
                return Integer.parseInt(s) > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    private enum Category {
        SERVER("server", "server"),
        CLIENT("client", "client"),
        INTEGRATION("client.integration", "integration");
        private final @NotNull String key;
        private final @Nullable String sprite;

        Category(@NotNull String key, @Nullable String sprite) {
            this.key = key;
            this.sprite = sprite;
        }

        public @NotNull String key() {
            return key;
        }

        public @Nullable String sprite() {
            return sprite;
        }
    }

}

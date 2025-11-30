package eu.darkcode.client.screen;

import eu.darkcode.BetterChaosMod;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.Settings;
import eu.darkcode.client.ClientSettings;
import eu.darkcode.client.hud.HudSettings;
import eu.darkcode.client.voting.integration.twitch.TwitchIntegrationSettings;
import eu.darkcode.client.widgets.OreInputOption;
import eu.darkcode.client.widgets.container.OrePanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static eu.darkcode.client.widgets.Ore.PASSWORD_FORMATTER;
import static eu.darkcode.client.widgets.OreSlider.*;

public class ConfigScreen extends BaseScreen {
    private final @NotNull Settings settings;
    private final @NotNull ClientSettings clientSettings;

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
        OrePanel panel = new OrePanel();

        serverConfig(panel);
        clientConfig(panel);

        this.layout.addBackButton();
        this.layout.addToContents(panel);
        super.init();
    }

    private void serverConfig(OrePanel panel) {
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

    private void clientConfig(OrePanel panel) {
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
        panel.addSlider(
                "client.activeEvents.maxEntries",
                null,
                hud.activeEventsMaxEntries(), 4, 12,
                FORMATTER_PLAIN,
                hud::activeEventsMaxEntries,
                true
        );
        panel.addCategory("client.poll", null);
        panel.addSlider(
                "client.poll.width",
                null,
                hud.pollWidth(), 150, 400,
                FORMATTER_PIXELS,
                hud::pollWidth,
                true
        );
        boolean enabledOptions = Minecraft.getInstance().level == null;
        panel.addCategory("integration", null);

        TwitchIntegrationSettings twitchIntegration = clientSettings.integration.twitchIntegration;

        OreInputOption channelName = panel.addTextInput("client.integration.twitch.channel", null, twitchIntegration.channel, a -> twitchIntegration.channel = a, enabledOptions);
        OreInputOption channelToken = panel.addTextInput("client.integration.twitch.token", null, twitchIntegration.token, a -> twitchIntegration.token = a, enabledOptions);
        channelToken.textInput().addFormatter(PASSWORD_FORMATTER);
    }

}

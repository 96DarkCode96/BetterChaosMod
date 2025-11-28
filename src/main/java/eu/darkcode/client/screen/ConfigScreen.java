package eu.darkcode.client.screen;

import eu.darkcode.BetterChaosMod;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.Settings;
import eu.darkcode.client.ClientSettings;
import eu.darkcode.client.hud.HudSettings;
import eu.darkcode.client.widgets.ore.OrePanel;
import eu.darkcode.client.widgets.ore.layout.LinearLayout;
import eu.darkcode.client.widgets.ore.option.OreOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static eu.darkcode.client.widgets.ore.OreSlider.*;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final @NotNull Settings settings;
    private final @NotNull ClientSettings clientSettings;

    public ConfigScreen(OptionsScreen parent) {
        super(Component.translatable("betterchaosmod.config.title"));
        this.parent = parent;
        settings = BetterChaosMod.instance().settings();
        clientSettings = BetterChaosModClient.instance().settings();
    }

    @Override
    public void onClose() {
        // [SAVE SERVER SETTINGS]
        BetterChaosMod.instance().saveSettings();
        // [SAVE CLIENT SETTINGS]
        BetterChaosModClient.instance().saveSettings();
        // [OPEN PREVIOUS SCREEN]
        assert minecraft != null;
        minecraft.setScreen(parent);
    }

    @Override
    protected void init() {
        OrePanel panel = addRenderableWidget(new OrePanel(new LinearLayout(1)));
        panel.setY(30);
        panel.setX(0);
        panel.setWidth(500);

        serverConfig(panel);
        clientConfig(panel);

        panel.arrangeElements();
    }

    private void serverConfig(OrePanel panel) {
        boolean enabledOptions = Minecraft.getInstance().level == null;
        panel.addWidget(OreOption.slider(
                "server.duration.event",
                "server.duration.event_desc",
                settings.eventDuration / 20, 10, 120,
                FORMATTER_SECONDS,
                a -> settings.eventDuration = a * 20
        )).setActive(enabledOptions);
        panel.addWidget(OreOption.slider(
                "server.duration.timer",
                "server.duration.timer_desc",
                settings.timerDuration / 20, 15, 300,
                FORMATTER_SECONDS,
                a -> settings.timerDuration = a * 20
        )).setActive(enabledOptions);
    }

    private void clientConfig(OrePanel panel) {
        final HudSettings hud = clientSettings.hud;
        panel.addElement(OreOption.category("client.timer", null));
        panel.addWidget(OreOption.slider(
                "client.timer.height",
                null,
                hud.timerHeight(), 5, 40,
                FORMATTER_PIXELS,
                hud::timerHeight
        ));
        panel.addWidget(OreOption.slider(
                "client.activeEvents.maxEntries",
                null,
                hud.activeEventsMaxEntries(), 4, 12,
                FORMATTER_PLAIN,
                hud::activeEventsMaxEntries
        ));
        panel.addElement(OreOption.category("client.poll", null));
        panel.addWidget(OreOption.slider(
                "client.poll.width",
                null,
                hud.pollWidth(), 150, 400,
                FORMATTER_PIXELS,
                hud::pollWidth
        ));
    }

}

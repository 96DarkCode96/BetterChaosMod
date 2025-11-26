package eu.darkcode.client.screen;

import eu.darkcode.BetterChaosMod;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.Settings;
import eu.darkcode.client.ClientSettings;
import eu.darkcode.client.hud.HudSettings;
import eu.darkcode.client.widgets.OreSlider;
import eu.darkcode.client.widgets.ore.OrePanel;
import eu.darkcode.client.widgets.ore.OreVerticalPanel;
import eu.darkcode.client.widgets.styles.Constraint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

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
        OrePanel panel = new OreVerticalPanel();
        panel.setY(30);
        panel.stretchHorizontal(500, width);

        serverConfig(panel);
        clientConfig(panel);

        panel.arrangeElements();
        addRenderableWidget(panel);
    }

    private void serverConfig(OrePanel panel) {
        boolean enabledOptions = Minecraft.getInstance().level == null;
        panel.addWidget(new OreSlider(
                Component.translatable("betterchaosmod.option.server.duration.event"),
                Component.translatable("betterchaosmod.option.server.duration.event_desc"),
                settings.eventDuration / 20, 10, 120,
                a -> Component.literal(a + "s"),
                a -> settings.eventDuration = a * 20
        )).padding(new Constraint(0, 0, 0, 1)).enabled = enabledOptions;
        panel.addWidget(new OreSlider(
                Component.translatable("betterchaosmod.option.server.duration.timer"),
                Component.translatable("betterchaosmod.option.server.duration.timer_desc"),
                settings.timerDuration / 20, 15, 300,
                a -> Component.literal(a + "s"),
                a -> settings.timerDuration = a * 20
        )).padding(new Constraint(0, 0, 0, 1)).enabled = enabledOptions;
    }

    private void clientConfig(OrePanel panel) {
        final HudSettings hud = clientSettings.hud;
        panel.addWidget(new OreSlider(
                Component.translatable("betterchaosmod.option.client.timer.height"),
                null,
                hud.timerHeight(), 5, 40,
                a -> Component.literal(a + "px"),
                hud::timerHeight
        )).padding(new Constraint(0, 0, 0, 1));
        panel.addWidget(new OreSlider(
                Component.translatable("betterchaosmod.option.client.activeEvents.maxEntries"),
                null,
                hud.activeEventsMaxEntries(), 4, 12,
                a -> Component.literal(String.valueOf(a)),
                hud::activeEventsMaxEntries
        )).padding(new Constraint(0, 0, 0, 1));
        panel.addWidget(new OreSlider(
                Component.translatable("betterchaosmod.option.client.poll.width"),
                null,
                hud.pollWidth(), 150, 400,
                a -> Component.literal(a + "px"),
                hud::pollWidth
        ));
    }

}

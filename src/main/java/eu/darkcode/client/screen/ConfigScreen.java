package eu.darkcode.client.screen;

import eu.darkcode.BetterChaosMod;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.Settings;
import eu.darkcode.client.ClientSettings;
import eu.darkcode.client.hud.HudSettings;
import eu.darkcode.client.widgets.OreSlider;
import eu.darkcode.client.widgets.Panel;
import eu.darkcode.client.widgets.SliderWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;

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
        final boolean inGame = Minecraft.getInstance().level != null;
        final HudSettings hud = clientSettings.hud;

        Panel panel = new Panel();
        panel.setY(10);
        panel.stretchHorizontal(500, width);

        panel.addChild(new OreSlider(
                Component.translatable("betterchaosmod.option.server.duration.event"),
                Component.translatable("betterchaosmod.option.server.duration.event_desc"),
                settings.eventDuration / 20, 10, 120,
                a -> Component.literal(a + "s"),
                a -> settings.eventDuration = a * 20
        ));
        panel.addChild(new OreSlider(
                Component.translatable("betterchaosmod.option.server.duration.timer"),
                Component.translatable("betterchaosmod.option.server.duration.timer_desc"),
                settings.timerDuration / 20, 15, 300,
                a -> Component.literal(a + "s"),
                a -> settings.timerDuration = a * 20
        ));

        //addIntSlider(panel, "client.timer.height", hud.timerHeight(), 5, 40, hud::timerHeight);
        //addIntSlider(panel, "client.activeEvents.maxEntries", hud.activeEventsMaxEntries(), 4, 12, hud::activeEventsMaxEntries);
        //addIntSlider(panel, "client.poll.width", hud.pollWidth(), 150, 400, hud::pollWidth);

        //this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).width(200).build());

        panel.arrangeElements();
        addRenderableOnly(panel);
        panel.visitWidgets(this::addRenderableWidget);
    }

    @NotNull
    private SliderWidget addIntSlider(@NotNull LinearLayout rowHelper, @NotNull String tKey, int value, int min, int max, @NotNull IntConsumer set) {
        return rowHelper.addChild(new SliderWidget(0, 0, 150, 20, "betterchaosmod.option." + tKey, value, min, max, a -> Component.literal("" + (int) Math.floor(a)), a -> set.accept((int) Math.floor(a))));
    }
}

package eu.darkcode.client.screen;

import eu.darkcode.BetterChaosMod;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.Settings;
import eu.darkcode.client.ClientSettings;
import eu.darkcode.client.hud.HudSettings;
import eu.darkcode.client.widgets.OrePanel;
import eu.darkcode.client.widgets.ScreenLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static eu.darkcode.client.widgets.OreSlider.*;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final @NotNull Settings settings;
    private final @NotNull ClientSettings clientSettings;
    private final ScreenLayout layout = new ScreenLayout(this);

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
        OrePanel panel = new OrePanel();

        serverConfig(panel);
        clientConfig(panel);

        this.layout.addToContents(panel);
        this.layout.visitChildren(this::addChildren);

        repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    private void addChildren(LayoutElement element) {
        if (element instanceof AbstractWidget aw)
            this.addRenderableWidget(aw);
        else if (element instanceof Renderable r)
            this.addRenderableOnly(r);
        if (element instanceof Layout l)
            l.visitChildren(this::addChildren);
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
    }

}

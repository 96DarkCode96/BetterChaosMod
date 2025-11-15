package eu.darkcode.client.screen;

import eu.darkcode.Settings;
import eu.darkcode.client.widgets.SliderWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 33);

    public ConfigScreen(OptionsScreen parent) {
        super(Component.translatable("betterchaosmod.config.title"));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        // TODO: Save settings
        assert minecraft != null;
        minecraft.setScreen(parent);
    }

    @Override
    protected void init() {
        boolean inGame = Minecraft.getInstance().level != null;
        SliderWidget eventDuration = new SliderWidget(this.width / 2 - 160, 50, 150, 20, "betterchaosmod.option.eventDuration", Settings.eventDuration / 20d, 10, 120, a -> Component.literal("" + Math.floor(a)), a -> Settings.eventDuration = (int) (a * 20));
        SliderWidget timerDuration = new SliderWidget(this.width / 2 + 10, 50, 150, 20, "betterchaosmod.option.timerDuration", Settings.timerDuration / 20d, 15, 300, a -> Component.literal("" + Math.floor(a)), a -> Settings.timerDuration = (int) (a * 20));

        if (inGame) {
            eventDuration.active = false;
            timerDuration.active = false;
        }

        LinearLayout linearLayout = this.layout.addToHeader(LinearLayout.vertical().spacing(8));
        linearLayout.addChild(new StringWidget(this.title, this.font), LayoutSettings::alignHorizontallyCenter);

        LinearLayout linearLayout2 = linearLayout.addChild(LinearLayout.horizontal()).spacing(8);
        linearLayout2.addChild(eventDuration);
        linearLayout2.addChild(timerDuration);

        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();

        GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(2);
        // TODO: add other options

        this.layout.addToContents(gridLayout);
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).width(200).build());
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
    }
}

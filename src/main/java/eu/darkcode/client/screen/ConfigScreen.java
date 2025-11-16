package eu.darkcode.client.screen;

import eu.darkcode.BetterChaosMod;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.Settings;
import eu.darkcode.UpdateStatus;
import eu.darkcode.client.widgets.SliderWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 33);
    private final @NotNull Settings settings;

    public ConfigScreen(OptionsScreen parent) {
        super(Component.translatable("betterchaosmod.config.title"));
        this.parent = parent;
        settings = BetterChaosMod.instance().settings();
    }

    @Override
    public void onClose() {
        // [SAVE SERVER SETTINGS]
        BetterChaosMod.instance().saveSettings();
        // TODO: Save client settings
        assert minecraft != null;
        minecraft.setScreen(parent);
    }

    @Override
    protected void init() {
        boolean inGame = Minecraft.getInstance().level != null;
        SliderWidget eventDuration = new SliderWidget(this.width / 2 - 160, 50, 150, 20, "betterchaosmod.option.eventDuration", settings.eventDuration / 20d, 10, 120, a -> Component.literal("" + Math.floor(a)), a -> settings.eventDuration = (int) (a * 20));
        SliderWidget timerDuration = new SliderWidget(this.width / 2 + 10, 50, 150, 20, "betterchaosmod.option.timerDuration", settings.timerDuration / 20d, 15, 300, a -> Component.literal("" + Math.floor(a)), a -> settings.timerDuration = (int) (a * 20));

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

        if (BetterChaosModClient.instance().updateStatus == UpdateStatus.OUTDATED) {
            rowHelper.addChild(Button.builder(Component.translatable("betterchaosmod.option.update.outdated"), button ->
                    ConfirmLinkScreen.confirmLinkNow(this, UpdateStatus.UPDATE_LINK)).build());
        }

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

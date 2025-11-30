package eu.darkcode.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class OreInputOption extends LinearLayout implements Renderable {

    private final @NotNull OreTextInput oreTextInput;

    public OreInputOption(@NotNull Component name, @Nullable Component description, @NotNull String text, @NotNull Consumer<String> valueConsumer, boolean enabled) {
        super(0, 0, Orientation.VERTICAL);
        spacing(4).defaultCellSetting().alignHorizontallyLeft().paddingHorizontal(18);

        addChild(new OreLabel(name, Minecraft.getInstance().font), a -> a.paddingTop(13)).setColor(0xFFFFFFFF);

        this.oreTextInput = new OreTextInput(text, valueConsumer);
        this.oreTextInput.active = enabled;

        if (description == null) {
            addChild(this.oreTextInput, a -> a.paddingBottom(12));
        } else {
            addChild(this.oreTextInput);
            addChild(new OreLabel(description, Minecraft.getInstance().font), a -> a.paddingBottom(12)).setColor(0xFFAAAAAA);
        }
    }

    public @NotNull OreTextInput textInput() {
        return oreTextInput;
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        int right = getX() + getWidth();
        g.fill(getX(), getY(), right, getY() + 1, 0xFF5A5B5D);
        g.fill(getX(), getY() + 1, right, getY() + getHeight(), 0xFF48494B);
    }
}

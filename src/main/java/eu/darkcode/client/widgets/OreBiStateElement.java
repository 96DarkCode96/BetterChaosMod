package eu.darkcode.client.widgets;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import eu.darkcode.client.widgets.utils.SpriteAtlas;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public abstract class OreBiStateElement extends OreWidget {
    private boolean value;

    protected OreBiStateElement(int x, int y, int width, int height, boolean value) {
        super(x, y, width, height);
        this.value = value;
    }

    protected abstract void onValueChanged(boolean newValue);

    @Override
    protected void renderWidget(@NotNull GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        Identifier resourceLocation = getSpriteAtlas().get(isHovered(), isFocused(), isActive());
        g.blitSprite(RenderPipelines.GUI_TEXTURED, resourceLocation, getX(), getY(), getWidth(), getHeight(), 0xFFFFFFFF);
        if (this.isHovered())
            g.requestCursor(this.isActive() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
    }

    @Override
    public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        value = !value;
        onValueChanged(value);
    }

    @NotNull
    protected abstract SpriteAtlas getSpriteAtlas();

    public boolean getValue() {
        return value;
    }

}

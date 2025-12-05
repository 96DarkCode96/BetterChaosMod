package eu.darkcode.client.widgets.utils;

import eu.darkcode.utils.Utils;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record SpriteAtlas(@NotNull ResourceLocation normal, @Nullable ResourceLocation hovered,
                          @Nullable ResourceLocation disabled, @Nullable ResourceLocation focused) {

    public SpriteAtlas(@NotNull String basePath, @Nullable String hoveredPath, @Nullable String disabledPath, @Nullable String focusedPath) {
        this(
                Utils.resourceLocation(basePath),
                hoveredPath != null ? Utils.resourceLocation(hoveredPath) : null,
                disabledPath != null ? Utils.resourceLocation(disabledPath) : null,
                focusedPath != null ? Utils.resourceLocation(focusedPath) : null
        );
    }

    public SpriteAtlas(@NotNull String basePath) {
        this(
                Utils.resourceLocation(basePath),
                Utils.resourceLocation(basePath + "_hover"),
                Utils.resourceLocation(basePath + "_disabled"),
                Utils.resourceLocation(basePath + "_focused")
        );
    }

    @NotNull
    public ResourceLocation get(boolean hovered, boolean focused, boolean active) {
        if (!active && this.disabled != null)
            return this.disabled;
        if (focused && this.focused != null)
            return this.focused;
        if (hovered && this.hovered != null)
            return this.hovered;
        return this.normal;
    }
}

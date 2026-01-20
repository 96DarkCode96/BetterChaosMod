package eu.darkcode.client.widgets.utils;

import eu.darkcode.utils.Utils;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record SpriteAtlas(@NotNull Identifier normal, @Nullable Identifier hovered,
                          @Nullable Identifier disabled, @Nullable Identifier focused) {

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
    public Identifier get(boolean hovered, boolean focused, boolean active) {
        if (!active && this.disabled != null)
            return this.disabled;
        if (focused && this.focused != null)
            return this.focused;
        if (hovered && this.hovered != null)
            return this.hovered;
        return this.normal;
    }
}

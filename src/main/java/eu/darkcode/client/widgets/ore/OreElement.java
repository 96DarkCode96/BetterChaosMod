package eu.darkcode.client.widgets.ore;

import eu.darkcode.client.widgets.styles.BoundingBox;
import eu.darkcode.client.widgets.styles.Spacing;
import net.minecraft.client.gui.components.Renderable;
import org.jetbrains.annotations.NotNull;

public interface OreElement extends Renderable, Spacing {
    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    default int getRight() {
        return getX() + getWidth();
    }

    default int getBottom() {
        return getY() + getHeight();
    }

    default void calculateBoundingBox(@NotNull BoundingBox box) {
        box.applyMargin(margin());
    }

}

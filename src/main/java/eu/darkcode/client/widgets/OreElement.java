package eu.darkcode.client.widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public interface OreElement {
    int getX();

    void setX(int i);

    int getY();

    void setY(int i);

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    default int getRight() {
        return this.getX() + this.getWidth();
    }

    default int getBottom() {
        return this.getY() + this.getHeight();
    }

    default void setPosition(int i, int j) {
        this.setX(i);
        this.setY(j);
    }

    default void visitWidgets(@NotNull Consumer<OreWidget> consumer) {
        // ignored
    }
}

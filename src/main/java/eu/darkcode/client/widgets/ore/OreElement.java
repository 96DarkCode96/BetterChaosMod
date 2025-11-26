package eu.darkcode.client.widgets.ore;

import eu.darkcode.client.widgets.styles.Constraint;
import net.minecraft.client.gui.components.Renderable;
import org.jetbrains.annotations.NotNull;

public interface OreElement extends Renderable {
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

    @NotNull
    default Constraint padding() {
        return Constraint.ZERO;
    }

    @NotNull
    default Constraint margin() {
        return Constraint.ZERO;
    }

    default int paddingLeft() {
        return padding().left();
    }

    default int paddingTop() {
        return padding().top();
    }

    default int paddingRight() {
        return padding().right();
    }

    default int paddingBottom() {
        return padding().bottom();
    }

    default int paddingHorizontal() {
        return padding().left() + padding().right();
    }

    default int paddingVertical() {
        return padding().top() + padding().bottom();
    }

    default int marginLeft() {
        return margin().left();
    }

    default int marginTop() {
        return margin().top();
    }

    default int marginRight() {
        return margin().right();
    }

    default int marginBottom() {
        return margin().bottom();
    }

    default int marginHorizontal() {
        return margin().left() + margin().right();
    }

    default int marginVertical() {
        return margin().top() + margin().bottom();
    }

}

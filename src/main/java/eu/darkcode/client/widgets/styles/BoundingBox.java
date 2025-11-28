package eu.darkcode.client.widgets.styles;

import org.jetbrains.annotations.NotNull;

public final class BoundingBox {
    private int x;
    private int y;
    private int width;
    private int height;

    public BoundingBox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void applyMargin(@NotNull EdgeInsets edgeInsets) {
        this.x += edgeInsets.left();
        this.y += edgeInsets.top();
        this.width -= edgeInsets.left() + edgeInsets.right();
        this.height -= edgeInsets.top() + edgeInsets.bottom();
    }

}

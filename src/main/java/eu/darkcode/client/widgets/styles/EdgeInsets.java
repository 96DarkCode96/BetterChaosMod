package eu.darkcode.client.widgets.styles;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record EdgeInsets(int left, int top, int right, int bottom) {

    public static final @NotNull EdgeInsets ZERO = new EdgeInsets(0);
    public static final @NotNull EdgeInsets ONE = new EdgeInsets(1);

    public EdgeInsets(int padding) {
        this(padding, padding, padding, padding);
    }

    public EdgeInsets(int horizontal, int vertical) {
        this(horizontal, vertical, horizontal, vertical);
    }

    @Contract("_ -> new")
    public EdgeInsets left(int left) {
        return new EdgeInsets(left, top, right, bottom);
    }

    @Contract("_ -> new")
    public EdgeInsets top(int top) {
        return new EdgeInsets(left, top, right, bottom);
    }

    @Contract("_ -> new")
    public EdgeInsets right(int right) {
        return new EdgeInsets(left, top, right, bottom);
    }

    @Contract("_ -> new")
    public EdgeInsets bottom(int bottom) {
        return new EdgeInsets(left, top, right, bottom);
    }

    @Contract("_ -> new")
    public EdgeInsets vertical(int padding) {
        return new EdgeInsets(left, padding, right, padding);
    }

    @Contract("_ -> new")
    public EdgeInsets horizontal(int padding) {
        return new EdgeInsets(padding, top, padding, bottom);
    }
}

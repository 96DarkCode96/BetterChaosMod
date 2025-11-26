package eu.darkcode.client.widgets.styles;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Constraint(int left, int top, int right, int bottom) {

    public static final @NotNull Constraint ZERO = new Constraint(0);
    public static final @NotNull Constraint ONE = new Constraint(1);

    public Constraint(int padding) {
        this(padding, padding, padding, padding);
    }

    public Constraint(int horizontal, int vertical) {
        this(horizontal, vertical, horizontal, vertical);
    }

    @Contract("_ -> new")
    public Constraint left(int left) {
        return new Constraint(left, top, right, bottom);
    }

    @Contract("_ -> new")
    public Constraint top(int top) {
        return new Constraint(left, top, right, bottom);
    }

    @Contract("_ -> new")
    public Constraint right(int right) {
        return new Constraint(left, top, right, bottom);
    }

    @Contract("_ -> new")
    public Constraint bottom(int bottom) {
        return new Constraint(left, top, right, bottom);
    }

    @Contract("_ -> new")
    public Constraint vertical(int padding) {
        return new Constraint(left, padding, right, padding);
    }

    @Contract("_ -> new")
    public Constraint horizontal(int padding) {
        return new Constraint(padding, top, padding, bottom);
    }
}

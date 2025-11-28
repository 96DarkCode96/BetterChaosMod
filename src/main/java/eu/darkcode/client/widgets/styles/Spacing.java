package eu.darkcode.client.widgets.styles;

import org.jetbrains.annotations.NotNull;

public interface Spacing {
    @NotNull
    default EdgeInsets padding() {
        return EdgeInsets.ZERO;
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

    @NotNull
    default EdgeInsets margin() {
        return EdgeInsets.ZERO;
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

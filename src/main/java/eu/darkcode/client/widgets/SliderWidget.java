package eu.darkcode.client.widgets;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Function;

public class SliderWidget extends AbstractSliderButton {

    private final double min;
    private final double max;
    private final String tKey;
    private final Function<Double, Component> format;
    private final Consumer<Double> change;

    public SliderWidget(int x, int y, int width, int height, String tKey, double value, double min, double max, Function<Double, Component> format, Consumer<Double> change) {
        super(x, y, width, height, Component.empty(), asFraction(value, min, max));
        this.tKey = tKey;
        this.min = min;
        this.max = max;
        this.format = format;
        this.change = change;
        this.updateMessage();
    }

    public static double asFraction(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    @Override
    protected void updateMessage() {
        setMessage(Component.translatable(tKey, format.apply(asValue())));
    }

    @Override
    protected void applyValue() {
        change.accept(asValue());
    }

    public double asValue() {
        return min + value * (max - min);
    }
}

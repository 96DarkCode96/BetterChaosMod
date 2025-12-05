package eu.darkcode.client.widgets.impl;

import eu.darkcode.client.widgets.OreBiStateElement;
import eu.darkcode.client.widgets.utils.SpriteAtlas;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class OreSwitch extends OreBiStateElement {

    private static final @NotNull SpriteAtlas SWITCH_ON = new SpriteAtlas("widget/switch_on", "widget/switch_on_hover", null, null);
    private static final @NotNull SpriteAtlas SWITCH_OFF = new SpriteAtlas("widget/switch_off", "widget/switch_off_hover", null, null);

    private final @NotNull Consumer<Boolean> onChange;

    public OreSwitch(boolean value, @NotNull Consumer<Boolean> onChange) {
        super(0, 0, 28, 16, value);
        this.onChange = onChange;
    }

    @Override
    protected void onValueChanged(boolean newValue) {
        onChange.accept(newValue);
    }

    @Override
    protected @NotNull SpriteAtlas getSpriteAtlas() {
        return getValue() ? SWITCH_ON : SWITCH_OFF;
    }
}

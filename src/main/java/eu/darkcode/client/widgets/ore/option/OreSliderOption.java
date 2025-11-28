package eu.darkcode.client.widgets.ore.option;

import eu.darkcode.client.widgets.ore.OreContainer;
import eu.darkcode.client.widgets.ore.OreLabel;
import eu.darkcode.client.widgets.ore.OreSlider;
import eu.darkcode.client.widgets.ore.layout.LinearLayout;
import eu.darkcode.client.widgets.styles.EdgeInsets;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class OreSliderOption extends OreOption {

    private final @NotNull Component name;

    public OreSliderOption(@NotNull Component name, @Nullable Component description, int value, int min, int max, @NotNull IntFunction<Component> formatter, @NotNull IntConsumer valueConsumer) {
        super();
        this.name = name;

        OreContainer row = addContainer(new LinearLayout(LinearLayout.Orientation.HORIZONTAL, 4));

        row.addElement(new OreLabel(name, 0xFFFFFFFF));
        final OreLabel valueLabel = row.addElement(new OreLabel(formatter.apply(value), 0xFFFFFFFF));

        if (description != null)
            addElement(new OreLabel(description, 0xFFC5C6C8));

        OreSlider sliderComponent = addWidget(new OreSlider(value, min, max, val -> {
            valueConsumer.accept(val);
            valueLabel.setText(formatter.apply(val));
        }));
        sliderComponent.margin(new EdgeInsets(0, 4, 0, -4));
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        super.updateNarration(narrationElementOutput);
        narrationElementOutput.add(NarratedElementType.TITLE, name);
    }

}
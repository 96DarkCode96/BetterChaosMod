package eu.darkcode.client.widgets.ore.option;

import eu.darkcode.client.widgets.ore.OreContainer;
import eu.darkcode.client.widgets.ore.layout.Layout;
import eu.darkcode.client.widgets.ore.layout.LinearLayout;
import eu.darkcode.client.widgets.styles.EdgeInsets;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public abstract class OreOption extends OreContainer {

    private static final String TRANSLATE_PREFIX = "betterchaosmod.option.";
    private static final String TRANSLATE_CATEGORY_PREFIX = "betterchaosmod.option.category.";

    protected OreOption() {
        this(new LinearLayout(4));
    }

    protected OreOption(@NotNull Layout layout) {
        super(layout);
        padding(new EdgeInsets(18, 13, 18, 12));
    }

    @NotNull
    @Contract("_, _ -> new")
    public static OreOptionCategory category(@NotNull String name, @Nullable String description) {
        return category(Component.translatable(TRANSLATE_CATEGORY_PREFIX + name), description == null ? null : Component.translatable(TRANSLATE_CATEGORY_PREFIX + description));
    }

    @NotNull
    @Contract("_, _ -> new")
    public static OreOptionCategory category(@NotNull Component name, @Nullable Component description) {
        return new OreOptionCategory(name, description);
    }

    @NotNull
    @Contract("_, _, _, _, _, _, _ -> new")
    public static OreSliderOption slider(@NotNull String name, @Nullable String description, int value, int min, int max, @NotNull IntFunction<Component> formatter, @NotNull IntConsumer valueConsumer) {
        return slider(Component.translatable(TRANSLATE_PREFIX + name),
                description == null ? null : Component.translatable(TRANSLATE_PREFIX + description),
                value, min, max, formatter, valueConsumer);
    }

    @NotNull
    @Contract("_, _, _, _, _, _, _ -> new")
    public static OreSliderOption slider(@NotNull Component name, @Nullable Component description, int value, int min, int max, @NotNull IntFunction<Component> formatter, @NotNull IntConsumer valueConsumer) {
        return new OreSliderOption(name, description, value, min, max, formatter, valueConsumer);
    }

    @Override
    public void render(GuiGraphics g, int i, int j, float f) {
        g.fill(getX(), getY(), getRight(), getY() + 1, 0xFF5A5B5D);
        g.fill(getX(), getY() + 1, getRight(), getBottom(), 0xFF48494B);
        super.render(g, i, j, f);
    }

}

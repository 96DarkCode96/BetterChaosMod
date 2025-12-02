package eu.darkcode.client.widgets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public interface OreLayout extends OreElement {
    void visitChildren(@NotNull Consumer<OreElement> consumer);

    @Override
    default void visitWidgets(@NotNull Consumer<OreWidget> consumer) {
        this.visitChildren(layoutElement -> layoutElement.visitWidgets(consumer));
    }

    default void arrangeElements() {
        this.visitChildren(layoutElement -> {
            if (layoutElement instanceof OreLayout layout)
                layout.arrangeElements();
        });
    }
}

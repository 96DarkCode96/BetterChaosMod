package eu.darkcode.client;

import net.minecraft.world.entity.player.Input;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public final class SharedProperties {

    public static @Nullable UnaryOperator<Input> moveEditor = null;

    private SharedProperties() {
    }

}
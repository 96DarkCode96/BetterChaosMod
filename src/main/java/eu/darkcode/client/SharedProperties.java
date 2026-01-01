package eu.darkcode.client;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.player.Input;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public final class SharedProperties {

    public static @Nullable UnaryOperator<Input> moveEditor = null;
    public static @Nullable BiFunction<Float, SoundInstance, Float> soundVolumeEditor = null;
    public static @Nullable BiFunction<Float, SoundInstance, Float> soundPitchEditor = null;
    public static boolean jumpscareActive = false;
    public static boolean noDrops = false;
    public static boolean randomDrops = false;
    public static boolean invertedMouse = false;
    public static boolean noWorld = false;
    public static boolean xray = false;

    private SharedProperties() {
    }

}
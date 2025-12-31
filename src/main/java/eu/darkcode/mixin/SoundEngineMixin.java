package eu.darkcode.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.SharedProperties;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.BiFunction;

/**
 * @author _dark_code_
 * @date 30.12.2025
 **/
@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @ModifyVariable(
            method = "play",
            at = @At(
                    value = "STORE",
                    target = "Lnet/minecraft/client/sound/SoundEngine;calculateVolume(FLnet/minecraft/sound/SoundCategory;)F"
            ),
            ordinal = 2
    )
    private float modifyVolumeVariable(float h, @Local(argsOnly = true) SoundInstance soundInstance) {
        if (soundInstance != null && soundInstance.getLocation().equals(BetterChaosModClient.jumpscareId))
            return Math.clamp(BetterChaosModClient.jumpscareOption.get().floatValue(), 0f, 1f);

        BiFunction<Float, SoundInstance, Float> soundVolumeEditor = SharedProperties.soundVolumeEditor;
        if (soundVolumeEditor != null)
            h = soundVolumeEditor.apply(h, soundInstance);

        return h;
    }

    @ModifyVariable(
            method = "play",
            at = @At(
                    value = "STORE",
                    target = "Lnet/minecraft/client/sound/SoundEngine;calculatePitch(Lnet/minecraft/client/resources/sounds/SoundInstance;)F"
            ),
            ordinal = 3
    )
    private float modifyPitchVariable(float i, @Local(argsOnly = true) SoundInstance soundInstance) {
        BiFunction<Float, SoundInstance, Float> soundPitchEditor = SharedProperties.soundPitchEditor;
        if (soundPitchEditor != null)
            i = soundPitchEditor.apply(i, soundInstance);

        return i;
    }
}

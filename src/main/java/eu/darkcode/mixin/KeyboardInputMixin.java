package eu.darkcode.mixin;

import eu.darkcode.client.SharedProperties;
import net.minecraft.client.Options;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends ClientInput {

    @Final
    @Shadow
    private Options options;

    @Shadow
    private static float calculateImpulse(boolean bl, boolean bl2) {
        return 0;
    }

    /**
     * @author 96DarkCode96
     * @reason apply custom effects
     */
    @Override
    @Overwrite
    public void tick() {
        this.keyPresses = new Input(this.options.keyUp.isDown(), this.options.keyDown.isDown(), this.options.keyLeft.isDown(), this.options.keyRight.isDown(), this.options.keyJump.isDown(), this.options.keyShift.isDown(), this.options.keySprint.isDown());
        if (SharedProperties.moveEditor != null)
            this.keyPresses = SharedProperties.moveEditor.apply(this.keyPresses);
        float f = calculateImpulse(this.keyPresses.forward(), this.keyPresses.backward());
        float g = calculateImpulse(this.keyPresses.left(), this.keyPresses.right());
        this.moveVector = (new Vec2(g, f)).normalized();
    }
}

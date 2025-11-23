package eu.darkcode.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.darkcode.UpdateStatus;
import net.minecraft.WorldVersion;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @ModifyConstant(method = "createTitle", constant = @Constant(stringValue = "Minecraft"))
    private String changeTitleName(String original) {
        return "BetterChaosMod";
    }

    @WrapOperation(method = "createTitle", at = @At(value = "INVOKE", target = "Lnet/minecraft/WorldVersion;name()Ljava/lang/String;"))
    private String wrapVersion(WorldVersion worldVersion, Operation<String> operation) {
        try {
            return UpdateStatus.getModVersion();
        } catch (Exception e) {
            System.out.println("Failed to get mod version for title!");
            e.printStackTrace();
            return operation.call(worldVersion);
        }
    }
}

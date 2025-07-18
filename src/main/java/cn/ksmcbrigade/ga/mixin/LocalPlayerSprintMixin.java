package cn.ksmcbrigade.ga.mixin;

import cn.ksmcbrigade.ga.GunAura;
import com.tacz.guns.client.gameplay.LocalPlayerSprint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LocalPlayerSprint.class,remap = false)
public class LocalPlayerSprintMixin {

    @Inject(method = "getProcessedSprintStatus",at = @At("HEAD"), cancellable = true)
    public void getProcessedSprintStatus(boolean sprinting, CallbackInfoReturnable<Boolean> cir) {
        if (GunAura.CONFIG.isLoaded() && GunAura.ENABLED.get() && GunAura.NO_ADS_DELAY.get()) {
            cir.setReturnValue(sprinting);
            cir.cancel();
        }
    }
}

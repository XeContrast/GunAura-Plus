package cn.ksmcbrigade.ga.mixin;


import cn.ksmcbrigade.ga.GunAura;
import com.tacz.guns.client.event.CameraSetupEvent;
import net.minecraftforge.client.event.ViewportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CameraSetupEvent.class,remap = false)
public class CameraSetupEventMixin {
    @Inject(method = "applyCameraRecoil",at = @At("HEAD"), cancellable = true)
    private static void noRecoil(ViewportEvent.ComputeCameraAngles event, CallbackInfo ci) {
        if (GunAura.CONFIG.isLoaded() && GunAura.ENABLED.get() && GunAura.NO_RECOIL.get()) {
            ci.cancel();
        }
    }
}

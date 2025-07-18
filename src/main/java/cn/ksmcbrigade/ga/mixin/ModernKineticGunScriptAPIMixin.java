package cn.ksmcbrigade.ga.mixin;

import cn.ksmcbrigade.ga.GunAura;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ModernKineticGunScriptAPI.class,remap = false)
public abstract class ModernKineticGunScriptAPIMixin {
    @Redirect(method = "shootOnce",at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"))
    public float inj(float a, float b) {
        if (GunAura.CONFIG.isLoaded() && GunAura.ENABLED.get() && GunAura.NO_SCATTERING.get())
            return 0;
        else
            return Math.max(a,b);
    }

}

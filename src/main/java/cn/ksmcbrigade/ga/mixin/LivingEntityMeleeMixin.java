package cn.ksmcbrigade.ga.mixin;

import cn.ksmcbrigade.ga.GunAura;
import cn.ksmcbrigade.ga.network.GetClientConfigs;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.entity.shooter.LivingEntityMelee;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityMelee.class,remap = false)
public class LivingEntityMeleeMixin {
    @Shadow @Final private LivingEntity shooter;

    @Inject(method = {"getMeleeCoolDown","getTotalCooldownTime"},at = @At("RETURN"),cancellable = true)
    public void get(CallbackInfoReturnable<Long> cir){
        if(!IGun.mainhandHoldGun(this.shooter)) return;
        if(GunAura.CONFIG.isLoaded() && GetClientConfigs.getEnabled(this.shooter) && GunAura.NO_COOL_DOWN.get()) cir.setReturnValue(0L);
    }
}

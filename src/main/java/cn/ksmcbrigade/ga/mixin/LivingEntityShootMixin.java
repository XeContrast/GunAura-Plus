package cn.ksmcbrigade.ga.mixin;

import cn.ksmcbrigade.ga.GunAura;
import cn.ksmcbrigade.ga.network.GetClientConfigs;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.entity.shooter.LivingEntityShoot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityShoot.class,remap = false)
public class LivingEntityShootMixin {
    @Shadow @Final private LivingEntity shooter;

    @Inject(method = "getShootCoolDown",at = @At("RETURN"),cancellable = true)
    public void get(CallbackInfoReturnable<Long> cir){
        if(!IGun.mainhandHoldGun(this.shooter)) return;
        if(GunAura.CONFIG.isLoaded() && GunAura.NO_COOL_DOWN.get() && GetClientConfigs.getEnabled(this.shooter)) cir.setReturnValue(0L);
    }

    @Redirect(method = "shoot",at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;getCurrentAmmoCount(Lnet/minecraft/world/item/ItemStack;)I"))
    public int shoot(IGun instance, ItemStack stack){
        int ret = instance.getCurrentAmmoCount(stack);
        if(IGun.mainhandHoldGun(this.shooter) && GunAura.CONFIG.isLoaded() && GunAura.AMMO_FREE.get() && GetClientConfigs.getEnabled(this.shooter)) ret = Math.max(1,ret);
        return ret;
    }
}

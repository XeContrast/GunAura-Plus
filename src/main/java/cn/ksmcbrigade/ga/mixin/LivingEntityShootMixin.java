package cn.ksmcbrigade.ga.mixin;

import cn.ksmcbrigade.ga.GunAura;
import cn.ksmcbrigade.ga.network.GetClientConfigs;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.entity.shooter.LivingEntityShoot;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityShoot.class,remap = false)
public class LivingEntityShootMixin {
    @Shadow
    @Final
    private LivingEntity shooter;

    @Inject(method = "getShootCoolDown*", at = @At("RETURN"), cancellable = true)
    public void get(CallbackInfoReturnable<Long> cir) {
        if (!IGun.mainHandHoldGun(this.shooter)) return;
        if (GunAura.CONFIG.isLoaded() && GunAura.NO_COOL_DOWN.get() && GetClientConfigs.getEnabled(this.shooter))
            cir.setReturnValue(0L);
    }

    @Redirect(method = "shoot", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;getCurrentAmmoCount(Lnet/minecraft/world/item/ItemStack;)I"))
    public int shoot(IGun instance, ItemStack stack) {
        int ret = instance.getCurrentAmmoCount(stack);
        if (IGun.mainHandHoldGun(this.shooter) && GunAura.CONFIG.isLoaded() && GunAura.AMMO_FREE.get() && GetClientConfigs.getEnabled(this.shooter))
            ret = Math.max(1, ret);
        return ret;
    }

    @Redirect(method = "shoot",at = @At(value = "FIELD", target = "Lcom/tacz/guns/entity/shooter/ShooterDataHolder;sprintTimeS:F",opcode = Opcodes.GETFIELD))
    public float shoot(ShooterDataHolder instance) {
        if (GunAura.CONFIG.isLoaded() && GunAura.ENABLED.get() && GunAura.NO_ADS_DELAY.get())
            return 0;
        else
            return instance.sprintTimeS;
    }
}

package cn.ksmcbrigade.ga.mixin;

import cn.ksmcbrigade.ga.GunAura;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.gameplay.LocalPlayerShoot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LocalPlayerShoot.class,remap = false)
public class LocalPlayerShootMixin {
    @Inject(method = {"getClientShootCoolDown", "getCoolDown", "lambda$getClientShootCoolDown$3"}, at = @At("RETURN"), cancellable = true)
    public void get(CallbackInfoReturnable<Long> cir) {
        if (GunAura.CONFIG.isLoaded() && GunAura.ENABLED.get() && GunAura.NO_COOL_DOWN.get()) cir.setReturnValue(0L);
    }

    @Redirect(method = {"doShoot", "shoot"}, at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/IGun;getCurrentAmmoCount(Lnet/minecraft/world/item/ItemStack;)I"))
    public int shoot(IGun instance, ItemStack stack) {
        int ret = instance.getCurrentAmmoCount(stack);
        if (GunAura.CONFIG.isLoaded() && GunAura.ENABLED.get() && GunAura.AMMO_FREE.get()) ret = Math.max(1, ret);
        return ret;
    }
}

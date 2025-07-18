package cn.ksmcbrigade.ga.mixin;

import cn.ksmcbrigade.ga.GunAura;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.entity.shooter.LivingEntityAim;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.world.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LivingEntityAim.class,remap = false)
public class LivingEntityAimMixin {
    @Final
    @Shadow
    private ShooterDataHolder data;
    @Final
    @Shadow
    private LivingEntity shooter;
    @Redirect(method = "tickSprint",at = @At(value = "FIELD", target = "Lcom/tacz/guns/entity/shooter/ShooterDataHolder;isAiming:Z", opcode = Opcodes.GETFIELD))
    public boolean tickSprint(ShooterDataHolder instance) {
        if (GunAura.CONFIG.isLoaded() && GunAura.ENABLED.get() && GunAura.SPRINTING_SHOOT.get())
            return false;
        else
            return data.isAiming;
    }

    @Redirect(method = "tickSprint",at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/entity/ReloadState$StateType;isReloading()Z", opcode = Opcodes.GETFIELD))
    public boolean tickSprint2(ReloadState.StateType instance) {
        if (GunAura.CONFIG.isLoaded() && GunAura.ENABLED.get() && GunAura.SPRINTING_SHOOT.get())
            return false;
        else {
            IGunOperator operator = IGunOperator.fromLivingEntity(this.shooter);
            ReloadState reloadState = operator.getSynReloadState();
            return reloadState.getStateType().isReloading();
        }
    }
}

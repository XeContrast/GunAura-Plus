package cn.ksmcbrigade.ga.mixin;

import cn.ksmcbrigade.ga.GunAura;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.api.item.nbt.GunItemDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = GunItemDataAccessor.class,remap = false)
public interface GunItemDataMixin2 {
    /**
     * @author KSmc_brigade
     * @reason re
     */
    @Overwrite
    default FireMode getFireMode(ItemStack gun) {
        CompoundTag nbt = gun.getOrCreateTag();
        FireMode ret = nbt.contains("GunFireMode", 8) ? FireMode.valueOf(nbt.getString("GunFireMode")) : FireMode.UNKNOWN;
        if(GunAura.CONFIG.isLoaded() && GunAura.ENABLED.get() && GunAura.AUTO_MODE.get()) ret = FireMode.AUTO;
        return ret;
    }

    /**
     * @author KSmc_brigade
     * @reason re
     */
    @Overwrite
    default int getCurrentAmmoCount(ItemStack gun) {
        CompoundTag nbt = gun.getOrCreateTag();
        int ret = nbt.contains("GunCurrentAmmoCount", 3) ? nbt.getInt("GunCurrentAmmoCount") : 0;
        if(GunAura.CONFIG.isLoaded() && GunAura.ENABLED.get() && GunAura.AMMO_FREE.get()) ret = Math.max(1,ret);
        return ret;
    }
}

package cn.ksmcbrigade.ga.mixin;

import cn.ksmcbrigade.ga.client.GAClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    @Unique
    private static boolean ga$first = true;

    @Inject(method = "setShaderGlintAlpha(D)V", at = @At("HEAD"))
    private static void key(double p_268332_, CallbackInfo ci) {
        if (ga$first) {
            Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, GAClient.key);
            ga$first = false;
        }
    }
}

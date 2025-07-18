package cn.ksmcbrigade.ga.mixin;

import cn.ksmcbrigade.ga.event.PacketSendEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V",at = @At("HEAD"))
    public void send(Packet<?> p_129513_, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.register(new PacketSendEvent(p_129513_));
    }
}

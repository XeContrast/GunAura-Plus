package cn.ksmcbrigade.ga.network;

import cn.ksmcbrigade.ga.GunAura;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;

@Mod.EventBusSubscriber(modid = GunAura.MODID,value = Dist.DEDICATED_SERVER)
public class GetClientConfigs {

    public static boolean waiting = false;
    public static boolean enabled = false;

    public static boolean getEnabled(LivingEntity entity){
        if(!(entity instanceof ServerPlayer player)) return false;
        waiting = true;
        GunAura.channel.sendTo(new GunAura.Message("enabled",false),player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        while (waiting){
            try {
                Thread.yield();
            } catch (Exception | Error e) {
                break;
            }
        }
        return enabled;
    }
}

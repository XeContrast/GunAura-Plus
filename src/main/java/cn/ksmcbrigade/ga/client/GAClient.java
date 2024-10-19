package cn.ksmcbrigade.ga.client;

import cn.ksmcbrigade.ga.GunAura;
import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.ClientMessagePlayerShoot;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = GunAura.MODID,value = Dist.CLIENT)
public class GAClient {
    public static KeyMapping key = new KeyMapping("GunAura", InputConstants.KEY_F12,KeyMapping.CATEGORY_GAMEPLAY);

    @SubscribeEvent
    public static void input(InputEvent.Key event){
        if(key.isDown()){
            GunAura.ENABLED.set(!GunAura.ENABLED.get());
            Player player = Minecraft.getInstance().player;
            if(player!=null) player.displayClientMessage(Component.literal("GunAura: ").append(String.valueOf(GunAura.ENABLED.get())),true);
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event){
        if(GunAura.ENABLED.get()){
            Player player = event.player;
            if(IGun.mainhandHoldGun(player)){
                Vec3 aabb = player.getPosition(0);
                LivingEntity hurt = player.level().getNearestEntity(LivingEntity.class, TargetingConditions.DEFAULT.range(GunAura.RANGE.get()).selector((p)->p.getId()!=player.getId()),player,aabb.x,aabb.y,aabb.z,new AABB(aabb,aabb).inflate(GunAura.RANGE.get()));
                if(hurt!=null && Minecraft.getInstance().getConnection()!=null){
                    final boolean last = player.isShiftKeyDown();
                    player.setShiftKeyDown(true);
                    faceEntity(player,hurt,Minecraft.getInstance().getConnection().getConnection(),player.onGround());
                    NetworkHandler.CHANNEL.sendToServer(new ClientMessagePlayerShoot());
                    player.setShiftKeyDown(last);
                }
            }
        }
    }

    public static void faceEntity(Entity player, Entity targetEntity, Connection connection,boolean on) {

        double dX = targetEntity.getX() - player.getX();
        double dY = targetEntity.getY() - player.getY();
        double dZ = targetEntity.getZ() - player.getZ();
        double distanceXZ = Math.sqrt(dX * dX + dZ * dZ);
        float yaw = (float) Math.toDegrees(Math.atan2(dZ, dX)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(dY, distanceXZ));

        connection.send(new ServerboundMovePlayerPacket.Rot(yaw,pitch,on));
    }
}

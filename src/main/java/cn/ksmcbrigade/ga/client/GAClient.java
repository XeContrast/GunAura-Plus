package cn.ksmcbrigade.ga.client;

import cn.ksmcbrigade.ga.GunAura;
import cn.ksmcbrigade.ga.event.PacketSendEvent;
import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.ClientMessagePlayerShoot;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.profiling.jfr.event.PacketEvent;
import net.minecraft.util.profiling.jfr.event.PacketSentEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = GunAura.MODID,value = Dist.CLIENT)
public class GAClient {
    public static KeyMapping key = new KeyMapping("GunAura", InputConstants.KEY_F12, KeyMapping.CATEGORY_GAMEPLAY);

    @SubscribeEvent
    public static void input(InputEvent.Key event) {
        if (key.isDown()) {
            GunAura.ENABLED.set(!GunAura.ENABLED.get());
            Player player = Minecraft.getInstance().player;
            if (player != null)
                player.displayClientMessage(Component.literal("GunAura: ").append(String.valueOf(GunAura.ENABLED.get())), true);
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {
        if (GunAura.ENABLED.get() && GunAura.AURA.get() && GunAura.CONFIG.isLoaded()) {
            Player player = event.player;
            if (IGun.mainHandHoldGun(player)) {
                if (Minecraft.getInstance().getConnection() != null) {
                    if (findClosestTarget(player) instanceof LivingEntity hurt) {
                        final boolean last = player.isShiftKeyDown();
                        faceEntity(player, hurt, Minecraft.getInstance().getConnection().getConnection(), player.onGround());
                        NetworkHandler.CHANNEL.sendToServer(new ClientMessagePlayerShoot());
                        player.setShiftKeyDown(last);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void packetSend(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
    }


    private static Entity findClosestTarget(Entity player) {
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        if (player == null)
            return null;

        List<Entity> entities = player.level().getEntities(player, player.getBoundingBox().inflate(GunAura.RANGE.get()), entity ->
                !entity.isRemoved() &&
                        entity instanceof LivingEntity livingEntity && !livingEntity.isDeadOrDying() && entity != player
        );

        for (Entity entity : entities) {
            double distance = player.distanceTo(entity);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestEntity = entity;
            }
        }

        return closestEntity;
    }

    public static float wrapAngleTo180_float(float value)
    {
        value = value % 360.0F;

        if (value >= 180.0F)
        {
            value -= 360.0F;
        }

        if (value < -180.0F)
        {
            value += 360.0F;
        }

        return value;
    }


    private static float sqrt_double(double sqrt) {
        return (float) Math.sqrt(sqrt);
    }

    public static void faceEntity(Entity player,Entity target,Connection connection, boolean on) {
        if (target == null)
            return;

        double xSize = target.getX() - player.getX();
        double ySize = target.getY() + (double)(target.getEyeHeight() / 2.0f) - (player.getY() + (double)player.getEyeHeight());
        double zSize = target.getZ() - player.getZ();
        double theta = sqrt_double(xSize * xSize + zSize * zSize);
        float yaw = (float)(Math.atan2(zSize, xSize) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(ySize, theta) * 180.0 / Math.PI));

        float yaw1 = (player.xRotO + wrapAngleTo180_float(yaw - player.xRotO)) % 360.0f;
        float pitch1 = (player.yRotO + wrapAngleTo180_float(pitch - player.yRotO)) % 360.0f;

        connection.send(new ServerboundMovePlayerPacket.Rot(yaw1, pitch1, on));
    }

//        public static void faceEntity(Entity player, Entity targetEntity, Connection connection,boolean on) {
//
//            AABB playerBox = player.getBoundingBox();
//            AABB targetBox = targetEntity.getBoundingBox();
//
//            double playerX = player.getX() + playerBox.minX + playerBox.maxX;
//            double playerY = player.getY() + playerBox.minY + playerBox.maxY;
//            double playerZ = player.getZ() + playerBox.minZ + playerBox.maxZ;
//
//            double targetX = targetEntity.getX() + targetBox.minX + targetBox.maxX;
//            double targetY = targetEntity.getY() + targetBox.minY + targetBox.maxY;
//            double targetZ = targetEntity.getZ() + targetBox.minZ + targetBox.maxZ;
//
//            double dX = targetX - playerX;
//            double dY = targetY - playerY;
//            double dZ = targetZ - playerZ;
//            double distanceXZ = Math.sqrt(dX * dX + dZ * dZ);
//            float yaw = (float) Math.toDegrees(Math.atan2(dZ, dX)) - 90.0F;
//            float pitch = (float) -Math.toDegrees(Math.atan2(dY, distanceXZ));
//
//            connection.send(new ServerboundMovePlayerPacket.Rot(yaw, pitch, on));
//        }

}

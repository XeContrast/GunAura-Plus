package cn.ksmcbrigade.ga;

import cn.ksmcbrigade.ga.network.GetClientConfigs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(GunAura.MODID)
public class GunAura {

    public static final String MODID = "ga";

    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder().comment("GunAura Both Config");
    private static final ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder().comment("GunAura Client Config");

    //main

    public static final ForgeConfigSpec.IntValue RANGE = clientBuilder.comment("The range of the GunAura module,when the GunAura module enabled,it will get the nearest entity to attack in this range.").comment("范围").defineInRange("range",10,0,1000);

    public static final ForgeConfigSpec.BooleanValue ENABLED = clientBuilder.comment("The enable of the GunAura module.").define("enabled",false);

    //ex

    public static final ForgeConfigSpec.BooleanValue AURA = builder.comment("IDK").comment("The no Recoil").comment("杀戮光环").define("KillAura",false);
    public static final ForgeConfigSpec.BooleanValue AMMO_FREE = builder.comment("This module need both install this mod.").comment("The ammo free module of the GunAura module,the gun's the counts of ammo will at least 1 when this module enabled.").comment("无限火力").define("ammoFree",false);
    public static final ForgeConfigSpec.BooleanValue AUTO_MODE = clientBuilder.comment("The forced auto module of the GunAura module,the gun's fire mode will forced to the auto mode when this module enabled.").comment("强制自动模式(无论枪械类型,即使是RPG也可以)").define("autoMode",false);
    public static final ForgeConfigSpec.BooleanValue NO_COOL_DOWN = builder.comment("This module need both install this mod.").comment("The no cool down module of the GunAura module,the gun's fire cool download will be to 0 when this module enabled.").comment("无攻击冷却").define("noCooldown",false);
    public static final ForgeConfigSpec.BooleanValue NO_RECOIL = builder.comment("IDK").comment("The no Recoil").comment("无后坐力").define("NoRecoil",false);
    public static final ForgeConfigSpec.BooleanValue NO_SCATTERING = builder.comment("IDK").comment("The no scattering").comment("无散射").define("NoScattering",false);

    public static final ForgeConfigSpec CONFIG = builder.build();
    public static final ForgeConfigSpec CLIENT_CONFIG = clientBuilder.build();

    public static final SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID,"sync"),()->"1.0.0",(a)->true,(b)->true);

    public GunAura() {
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT,CLIENT_CONFIG);

        channel.registerMessage(0,Message.class,Message::encode,Message::decode,(msg,context)->{
            if(msg.client.isEmpty()){
                GetClientConfigs.enabled = msg.status;
                GetClientConfigs.waiting = false;
            }
            else{
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()-> {
                    channel.sendToServer(new Message("",ENABLED.get()));
                    //System.out.println("Sent the status of the enable to server.");
                });
            }
            context.get().setPacketHandled(true);
        });

        System.out.println("GunAura mod loaded.");
    }

    public record Message(String client, boolean status){
        public static void encode(Message msg, FriendlyByteBuf buf){
            buf.writeUtf(msg.client());
            buf.writeBoolean(msg.status);
        }

        public static Message decode(FriendlyByteBuf friendlyByteBuf){
            return new Message(friendlyByteBuf.readUtf(),friendlyByteBuf.readBoolean());
        }
    }
}

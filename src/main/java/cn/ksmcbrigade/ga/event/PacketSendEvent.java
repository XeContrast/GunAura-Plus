package cn.ksmcbrigade.ga.event;

import net.minecraft.network.protocol.Packet;
import net.minecraftforge.eventbus.api.Event;

public class PacketSendEvent extends Event {
    private Packet<?> packet;

    public PacketSendEvent(Packet<?> p129513) {
        this.packet = p129513;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}

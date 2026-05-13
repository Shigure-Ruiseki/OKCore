package ruiseki.okcore;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.okcore.event.CooldownEvent;
import ruiseki.okcore.event.GuiItemToggleEvent;
import ruiseki.okcore.event.TickHandler;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.network.PacketHandler;
import ruiseki.okcore.network.packet.PacketCooldown;
import ruiseki.okcore.network.packet.PacketItemToggle;
import ruiseki.okcore.network.packet.PacketSound;
import ruiseki.okcore.network.packet.PacketSyncCategory;
import ruiseki.okcore.network.packet.PacketSyncEntry;
import ruiseki.okcore.network.packet.PacketSyncHome;
import ruiseki.okcore.proxy.CommonProxyComponent;

public class CommonProxy extends CommonProxyComponent {

    public CommonProxy() {}

    @Override
    public ModBase getMod() {
        return OKCore.instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);
        packetHandler.register(PacketSound.class);
        packetHandler.register(PacketCooldown.class);
        packetHandler.register(PacketItemToggle.class);
        packetHandler.register(PacketSyncHome.class);
        packetHandler.register(PacketSyncCategory.class);
        packetHandler.register(PacketSyncEntry.class);
    }

    @Override
    public void registerTickHandlers() {
        super.registerTickHandlers();
        FMLCommonHandler.instance()
            .bus()
            .register(TickHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(TickHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(CooldownEvent.INSTANCE);
        MinecraftForge.EVENT_BUS.register(GuiItemToggleEvent.INSTANCE);
    }
}

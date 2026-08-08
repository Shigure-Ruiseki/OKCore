package ruiseki.okcore.proxy;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.event.handler.CooldownEventHandler;
import ruiseki.okcore.event.handler.DataEventHandler;
import ruiseki.okcore.event.handler.InputEventHandler;
import ruiseki.okcore.event.handler.TileEventHandler;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.network.PacketHandler;
import ruiseki.okcore.network.packet.ButtonClickPacket;
import ruiseki.okcore.network.packet.PacketCooldown;
import ruiseki.okcore.network.packet.PacketItemToggle;
import ruiseki.okcore.network.packet.PacketSound;
import ruiseki.okcore.network.packet.PacketSyncCursorStack;
import ruiseki.okcore.network.packet.PacketUpdateRecipes;
import ruiseki.okcore.network.packet.PacketUpdateTags;
import ruiseki.okcore.network.packet.ValueNotifyPacket;

public class CommonProxy extends CommonProxyComponent {

    public CommonProxy() {}

    @Override
    public ModBase getMod() {
        return OKCore._instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);
        packetHandler.register(PacketSound.class);
        packetHandler.register(PacketCooldown.class);
        packetHandler.register(PacketItemToggle.class);
        packetHandler.register(PacketUpdateRecipes.class);
        packetHandler.register(PacketUpdateTags.class);
        packetHandler.register(PacketSyncCursorStack.class);
        packetHandler.register(ValueNotifyPacket.class);
        packetHandler.register(ButtonClickPacket.class);
    }

    @Override
    public void registerEventHooks() {
        super.registerEventHooks();
        FMLCommonHandler.instance()
            .bus()
            .register(DataEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(DataEventHandler.INSTANCE);

        MinecraftForge.EVENT_BUS.register(CooldownEventHandler.INSTANCE);

        MinecraftForge.EVENT_BUS.register(InputEventHandler.INSTANCE);
    }

    @Override
    public void registerTickHandlers() {
        super.registerTickHandlers();
        FMLCommonHandler.instance()
            .bus()
            .register(TileEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(TileEventHandler.INSTANCE);
    }
}

package ruiseki.okcore.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.GuiType;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

public class PacketOpenGuiWithData extends PacketCodec {

    @CodecField
    public int windowId;

    @CodecField
    public ResourceLocation id;

    @CodecField
    public ExtendedBuffer extraData;

    public PacketOpenGuiWithData() {}

    public PacketOpenGuiWithData(GuiType<?> type, int windowId, ExtendedBuffer extraData) {
        this(type.getRegistryName(), windowId, extraData);
    }

    public PacketOpenGuiWithData(ResourceLocation id, int windowId, ExtendedBuffer extraData) {
        this.id = id;
        this.windowId = windowId;
        this.extraData = extraData;
    }

    public final GuiType<?> getType() {
        return GuiType.REGISTRY.getValue(this.id);
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        GuiType<?> type = this.getType();
        if (type != null) {
            GuiScreens.create(type, Minecraft.getMinecraft(), this.windowId, this.extraData);
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }
}

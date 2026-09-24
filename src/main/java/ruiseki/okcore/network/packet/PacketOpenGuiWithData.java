package ruiseki.okcore.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.GuiType;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.inventory.container.ContainerExtended;
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
        GuiScreens.getScreenFactory(this.getType(), Minecraft.getMinecraft(), this.windowId)
            .ifPresent(f -> {
                ContainerExtended c = this.getType()
                    .create(this.windowId, player.inventory, this.extraData);
                @SuppressWarnings("unchecked")
                GuiScreen s = ((GuiScreens.ScreenConstructor<ContainerExtended, ?>) f).create(c, player.inventory);
                Minecraft.getMinecraft().thePlayer.openContainer = ((IContainerAccess<?>) s).getContainer();
                Minecraft.getMinecraft()
                    .displayGuiScreen(s);
            });
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }
}

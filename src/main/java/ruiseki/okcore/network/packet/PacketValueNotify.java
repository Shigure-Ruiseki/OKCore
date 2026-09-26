package ruiseki.okcore.network.packet;

import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.inventory.IValueNotifiable;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for sending a value from server to client.
 *
 * @see ruiseki.okcore.inventory.IValueNotifier
 * @see IValueNotifiable
 * @author rubensworks
 *
 */
public class PacketValueNotify extends PacketCodec {

    @CodecField
    private ResourceLocation containerType;
    @CodecField
    private int valueId;
    @CodecField
    private NBTTagCompound value;

    public PacketValueNotify() {

    }

    public PacketValueNotify(ContainerType<?> containerType, int valueId, NBTTagCompound value) {
        this.containerType = containerType.getRegistryName();
        this.valueId = valueId;
        this.value = value;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    protected boolean isContainerValid(IValueNotifiable container) {
        return Objects.equals(ContainerType.REGISTRY.getKey(container.getValueNotifiableType()), containerType);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        if (player.openContainer instanceof IValueNotifiable container) {
            if (isContainerValid(container)) {
                container.onUpdate(valueId, value);
            }
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        if (player.openContainer instanceof IValueNotifiable container) {
            if (isContainerValid(container)) {
                container.onUpdate(valueId, value);
            }
        }
    }

}

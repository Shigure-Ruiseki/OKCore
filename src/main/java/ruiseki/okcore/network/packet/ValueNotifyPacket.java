package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
public class ValueNotifyPacket extends PacketCodec {

    @CodecField
    private String guiModId;
    @CodecField
    private int guiId;
    @CodecField
    private int valueId;
    @CodecField
    private NBTTagCompound value;

    public ValueNotifyPacket() {

    }

    public ValueNotifyPacket(String guiModId, int guiId, int valueId, NBTTagCompound value) {
        this.guiModId = guiModId;
        this.guiId = guiId;
        this.valueId = valueId;
        this.value = value;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    protected boolean isContainerValid(IValueNotifiable container) {
        return container.getGuiId() == guiId && container.getGuiModId()
            .equals(guiModId);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {
        if (player.openContainer instanceof IValueNotifiable) {
            IValueNotifiable container = ((IValueNotifiable) player.openContainer);
            if (isContainerValid(container)) {
                container.onUpdate(valueId, value);
            }
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        if (player.openContainer instanceof IValueNotifiable) {
            IValueNotifiable container = ((IValueNotifiable) player.openContainer);
            if (isContainerValid(container)) {
                container.onUpdate(valueId, value);
            }
        }
    }

}

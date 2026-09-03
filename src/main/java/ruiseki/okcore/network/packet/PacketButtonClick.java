package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.inventory.container.button.IButtonClickAcceptor;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * Packet for notifying the server of a button click.
 *
 * @author rubensworks
 *
 */
public class PacketButtonClick extends PacketCodec {

    @CodecField
    private int buttonId;

    public PacketButtonClick() {

    }

    public PacketButtonClick(int buttonId) {
        this.buttonId = buttonId;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void actionClient(World world, EntityPlayer player) {

    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        if (player.openContainer instanceof IButtonClickAcceptor<?>acceptor) {
            acceptor.onButtonClick(buttonId);
        }
    }

}

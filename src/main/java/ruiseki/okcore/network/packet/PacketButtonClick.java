package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.inventory.container.button.IContainerButtonClickAcceptorServer;
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
    private String buttonId;

    public PacketButtonClick() {

    }

    public PacketButtonClick(String buttonId) {
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
        if (player.openContainer instanceof IContainerButtonClickAcceptorServer<?>acceptorServer) {
            acceptorServer.onButtonClick(buttonId);
        }
    }

}

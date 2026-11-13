package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.okcore.entity.permission.IPlayerPermission;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class PacketPermissionLevel extends PacketCodec {

    @CodecField
    private int level = 0;

    public PacketPermissionLevel() {

    }

    public PacketPermissionLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        ((IPlayerPermission) player).setPermissionLevel(level);
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }
}

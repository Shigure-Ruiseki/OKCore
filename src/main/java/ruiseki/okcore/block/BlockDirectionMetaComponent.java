package ruiseki.okcore.block;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.helper.DirectionHelpers;

public class BlockDirectionMetaComponent implements IBlockDirection {

    private final boolean use6Directions;

    public BlockDirectionMetaComponent() {
        this(false);
    }

    public BlockDirectionMetaComponent(boolean use6Directions) {
        this.use6Directions = use6Directions;
    }

    @Override
    public ForgeDirection getDirection(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (use6Directions) {
            return DirectionHelpers.metaToDirection6(meta);
        } else {
            return DirectionHelpers.metaToDirection4(meta);
        }
    }

    @Override
    public void setDirection(World world, int x, int y, int z, ForgeDirection direction) {
        if (direction == ForgeDirection.UNKNOWN) return;

        int currentMeta = world.getBlockMetadata(x, y, z);
        int newDirMeta;
        int mask;

        if (use6Directions) {
            newDirMeta = DirectionHelpers.direction6ToMeta(direction) & 7;
            mask = 7;
        } else {
            newDirMeta = DirectionHelpers.direction4ToMeta(direction) & 3;
            mask = 3;
        }

        int finalMeta = (currentMeta & ~mask) | newDirMeta;
        world.setBlockMetadataWithNotify(x, y, z, finalMeta, 3);
    }
}

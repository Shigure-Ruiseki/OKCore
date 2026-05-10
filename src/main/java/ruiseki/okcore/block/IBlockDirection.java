package ruiseki.okcore.block;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public interface IBlockDirection {

    ForgeDirection getDirection(IBlockAccess world, int x, int y, int z);

    void setDirection(IBlockAccess world, int x, int y, int z, ForgeDirection direction);
}

package ruiseki.okcore.item;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;

public class ItemHelpers {

    public static LazyOptional<IItemHandler> getItemHandler(Object object, ForgeDirection side) {
        return object instanceof TileEntity tile ? getItemHandler(tile, side) : LazyOptional.empty();
    }

    public static LazyOptional<IItemHandler> getItemHandler(TileEntity tile, ForgeDirection side) {
        return CapabilityHelpers.getCapability(tile, CapabilityItemHandler.ITEM_HANDLER, side);
    }

    public static LazyOptional<IItemHandler> getItemHandler(World world, BlockPos pos, ForgeDirection side) {
        return getItemHandler(pos.getTileEntity(world), side);
    }

    public static LazyOptional<IItemHandler> getItemHandler(World world, int x, int y, int z, ForgeDirection side) {
        return getItemHandler(world, new BlockPos(x, y, z), side);
    }
}

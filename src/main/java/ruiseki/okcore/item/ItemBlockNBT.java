package ruiseki.okcore.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.okcore.block.BlockTile;

/**
 * An extended {@link ItemBlockMetadata} that will add the NBT data that is stored inside
 * the item to the placed {@link TileEntity} for the blockState.
 * Subinstances of {@link BlockTile} will perform the inverse
 * operation, being
 * that broken blocks will save the NBT data inside the dropped {@link ItemBlock}.
 *
 * @author rubensworks
 *
 */
public class ItemBlockNBT extends ItemBlockMetadata {

    /**
     * Make a new instance.
     *
     * @param block The blockState instance.
     */
    public ItemBlockNBT(Block block) {
        super(block);
        this.setMaxStackSize(1);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int metadata) {
        if (super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) {
            TileEntity tile = world.getTileEntity(x, y, z);

            if (tile != null && stack.getTagCompound() != null) {
                tile.readFromNBT(stack.getTagCompound());
                itemStackDataToTile(stack, tile);
            }

            return true;
        }

        return false;
    }

    /**
     * Read additional info about the item into the tile.
     *
     * @param tile      The tile that is being created.
     * @param itemStack The item that is placed.
     */
    protected void itemStackDataToTile(ItemStack itemStack, TileEntity tile) {

    }
}

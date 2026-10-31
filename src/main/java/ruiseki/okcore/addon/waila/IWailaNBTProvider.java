
package ruiseki.okcore.addon.waila;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Provides server-side NBT data for WAILA tooltips.
 *
 * <p>
 * WAILA invokes this callback on the server when collecting additional
 * information for a targeted tile entity.
 *
 * <p>
 * Implementations should only write data required by the client tooltip.
 * Avoid exposing private information or synchronizing large NBT structures.
 *
 * @see IWailaTileInfoProvider
 */
public interface IWailaNBTProvider {

    /**
     * Writes additional WAILA data into the supplied NBT compound.
     *
     * <p>
     * The supplied compound is shared with WAILA and potentially other
     * providers. Implementations should use unique NBT keys to avoid
     * overwriting unrelated data.
     *
     * @param player the player requesting the information
     * @param tile   the targeted tile entity
     * @param tag    the mutable NBT compound
     * @param world  the world containing the tile entity
     * @param x      the block X coordinate
     * @param y      the block Y coordinate
     * @param z      the block Z coordinate
     */
    default void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {}
}

package ruiseki.okcore.guide;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public interface IGuideLinked {

    /**
     * @param world  - The world where the block is
     * @param x,     y, z - The block's location in the world
     * @param player - The player that triggered the method
     * @param stack  - The ingame book item
     * @return the key of the entry to open or null if no entry should be opened
     */
    @Nullable
    ResourceLocation getLinkedEntry(World world, int x, int y, int z, EntityPlayer player, ItemStack stack);
}

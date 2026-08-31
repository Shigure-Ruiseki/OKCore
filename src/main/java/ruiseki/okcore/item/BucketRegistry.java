package ruiseki.okcore.item;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.helper.ItemHelpers;

/**
 * This will take care of the logic of custom buckets, so they can be filled like other buckets.
 *
 * @author rubensworks
 *
 */
public class BucketRegistry implements IBucketRegistry {

    private final Map<Block, Item> items = Maps.newHashMap();
    private final Map<Item, FluidStack> fluidStacks = Maps.newHashMap();

    @Override
    public void registerBucket(Block block, Item item) {
        items.put(block, item);
    }

    @Override
    public void registerBucket(Item item, FluidStack fluidStack) {
        fluidStacks.put(item, fluidStack);
    }

    @Override
    public Map<Block, Item> getBlockItem() {
        return items;
    }

    @Override
    public Map<Item, FluidStack> getItemFluidStack() {
        return fluidStacks;
    }

    @Override
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBucketFill(FillBucketEvent event) {
        if (event == null || event.target == null || ItemHelpers.isEmpty(event.current)) {
            return;
        }

        ItemStack result = fillCustomBucket(event.world, event.target, event.current);
        if (!ItemHelpers.isEmpty(result)) {
            event.result = result;
            event.setResult(Event.Result.ALLOW);
        }
    }

    private ItemStack fillCustomBucket(World world, MovingObjectPosition pos, ItemStack current) {
        if (world == null || pos == null || ItemHelpers.isEmpty(current)) {
            return ItemHelpers.EMPTY;
        }

        int x = pos.blockX;
        int y = pos.blockY;
        int z = pos.blockZ;

        Block block = world.getBlock(x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);

        Item bucket = items.get(block);

        if (bucket != null && metadata == 0) {
            ItemStack bucketStack = new ItemStack(bucket);
            ItemStack containerItem = bucket.getContainerItem(bucketStack);

            if (!ItemHelpers.isEmpty(containerItem) && ItemHelpers.areItemsEqual(current, containerItem)) {
                world.setBlockToAir(x, y, z);
                return bucketStack;
            }
        }

        return ItemHelpers.EMPTY;
    }
}

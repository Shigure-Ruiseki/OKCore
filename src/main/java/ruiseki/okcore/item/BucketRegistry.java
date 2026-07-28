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
        ItemStack result = fillCustomBucket(event.world, event.target, event.current);
        if (result != null) {
            event.result = result;
            event.setResult(Event.Result.ALLOW);
        }
    }

    private ItemStack fillCustomBucket(World world, MovingObjectPosition pos, ItemStack current) {
        int x = pos.blockX;
        int y = pos.blockY;
        int z = pos.blockZ;

        Block block = world.getBlock(x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);

        Item bucket = items.get(block);

        if (bucket != null && metadata == 0) {
            ItemStack containerItem = bucket.getContainerItem(new ItemStack(bucket));
            if (current != null && containerItem != null && ItemStack.areItemStacksEqual(current, containerItem)) {
                world.setBlockToAir(x, y, z);
                return new ItemStack(bucket);
            }
        }

        return null;
    }
}

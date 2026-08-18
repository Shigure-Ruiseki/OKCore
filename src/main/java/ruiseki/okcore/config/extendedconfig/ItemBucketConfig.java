package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.item.IBucketRegistry;

/**
 * Config for buckets, extension of {@link ItemConfig}.
 *
 * @author rubensworks
 * @see ExtendedConfig
 * @see ItemConfig
 */
public abstract class ItemBucketConfig extends ItemConfig {

    /**
     * Make a new instance.
     *
     * @param mod            The mod instance.
     * @param enabled        If this should be enabled.
     * @param namedId        The unique name ID for the configurable.
     * @param comment        The comment to add in the config file for this configurable.
     * @param elementFactory Function factory to create the Item instance.
     */
    public ItemBucketConfig(ModBase mod, boolean enabled, String namedId, String comment,
        Function<ItemBucketConfig, Item> elementFactory) {
        super(mod, enabled, namedId, comment, (Function) elementFactory);
    }

    /**
     * Get the {@link net.minecraftforge.fluids.Fluid} this bucket can contain.
     *
     * @return the fluid.
     */
    public abstract Fluid getFluidInstance();

    /**
     * Get the {@link net.minecraft.block.Block} this bucket can place / pick up.
     *
     * @return the fluid blockState.
     */
    public abstract Block getFluidBlockInstance();

    @Override
    public void onRegistered() {
        Item item = this.getInstance();
        IBucketRegistry bucketRegistry = getMod().getRegistryManager()
            .getRegistry(IBucketRegistry.class);
        if (bucketRegistry != null && item != null) {
            if (getFluidInstance() != null) {
                FluidStack fluidStack = FluidRegistry
                    .getFluidStack(getFluidInstance().getName(), FluidContainerRegistry.BUCKET_VOLUME);

                ItemStack containerItem = item.getContainerItem() != null ? new ItemStack(item.getContainerItem())
                    : new ItemStack(Blocks.air);

                FluidContainerRegistry.registerFluidContainer(fluidStack, new ItemStack(item), containerItem);
                bucketRegistry.registerBucket(item, fluidStack);
            }

            if (getFluidBlockInstance() != null && getFluidBlockInstance() != Blocks.air) {
                bucketRegistry.registerBucket(getFluidBlockInstance(), item);
            }
        }
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}

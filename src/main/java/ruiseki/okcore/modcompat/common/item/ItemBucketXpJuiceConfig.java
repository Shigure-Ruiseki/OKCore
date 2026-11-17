package ruiseki.okcore.modcompat.common.item;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.extendedconfig.ItemBucketConfig;
import ruiseki.okcore.item.ItemBucketBase;
import ruiseki.okcore.modcompat.common.block.BlockFluidXpJuiceConfig;
import ruiseki.okcore.modcompat.common.fluid.FluidXpJuiceConfig;

public class ItemBucketXpJuiceConfig extends ItemBucketConfig {

    /**
     * The unique instance.
     */
    public static ItemBucketXpJuiceConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemBucketXpJuiceConfig() {
        super(
            OKCore._instance,
            true,
            "bucket_xpjuice",
            null,
            config -> new ItemBucketBase(BlockFluidXpJuiceConfig._instance.getInstance()));
    }

    @Override
    public Fluid getFluidInstance() {
        return FluidXpJuiceConfig._instance.getInstance();
    }

    @Override
    public Block getFluidBlockInstance() {
        return BlockFluidXpJuiceConfig._instance.getInstance();
    }

}

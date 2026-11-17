package ruiseki.okcore.modcompat.common.block;

import net.minecraft.block.material.Material;

import ruiseki.okcore.fluid.BlockFluidBase;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.modcompat.common.fluid.FluidXpJuiceConfig;

public class BlockFluidXpJuice extends BlockFluidBase {

    public BlockFluidXpJuice() {
        super(FluidXpJuiceConfig._instance.getInstance(), Material.water);

        if (MinecraftHelpers.isClientSide()) {
            this.setParticleColor(0.36078432F, 0.9647059F, 0.07450981F);
        }
    }
}

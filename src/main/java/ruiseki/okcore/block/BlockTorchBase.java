package ruiseki.okcore.block;

import net.minecraft.block.BlockTorch;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;

/**
 * Torch blockState that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public class BlockTorchBase extends BlockTorch
    implements IBlockPropertyProvider, IBlockGui, IBlockStateAction, IBlockTooltipProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    protected boolean hasGui = false;

    /**
     * Make a new blockState instance.
     *
     */
    public BlockTorchBase() {
        this.setHardness(0.0F);
        this.setLightLevel(0.9375F);
        this.setStepSound(soundTypeWood);
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }
}

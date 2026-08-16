package ruiseki.okcore.config.configurable;

import net.minecraft.block.BlockTorch;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * Torch blockState that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public class ConfigurableBlockTorch extends BlockTorch implements IConfigurableBlock, IBlockPropertyProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    protected BlockConfig eConfig = null;
    protected boolean hasGui = false;

    /**
     * Make a new blockState instance.
     *
     * @param eConfig Config for this blockState.
     */
    public ConfigurableBlockTorch(ExtendedConfig<BlockConfig> eConfig) {
        this.setConfig((BlockConfig) eConfig);
        this.setBlockName(eConfig.getUnlocalizedName());
        this.setBlockTextureName(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId());
        this.setHardness(0.0F);
        this.setLightLevel(0.9375F);
        this.setStepSound(soundTypeWood);
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }

    private void setConfig(BlockConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public BlockConfig getConfig() {
        return eConfig;
    }
}

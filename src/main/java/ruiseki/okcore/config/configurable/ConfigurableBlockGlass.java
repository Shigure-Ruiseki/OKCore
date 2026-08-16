package ruiseki.okcore.config.configurable;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

public class ConfigurableBlockGlass extends BlockGlass implements IConfigurableBlock {

    protected BlockConfig eConfig = null;
    protected boolean hasGui = false;

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    /**
     * Make a new blockState instance.
     *
     * @param eConfig          Config for this blockState.
     * @param material         Material of this blockState.
     * @param ignoreSimilarity Whether neighbor blocks of the same type should connect/render seamlessly.
     */
    public ConfigurableBlockGlass(ExtendedConfig<BlockConfig> eConfig, Material material, boolean ignoreSimilarity) {
        super(material, ignoreSimilarity);
        this.setConfig(eConfig);
        this.setBlockName(eConfig.getUnlocalizedName());
        this.setBlockTextureName(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId());
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }

    private void setConfig(@SuppressWarnings("rawtypes") ExtendedConfig eConfig) {
        this.eConfig = (BlockConfig) eConfig;
    }

    @Override
    public BlockConfig getConfig() {
        return eConfig;
    }
}

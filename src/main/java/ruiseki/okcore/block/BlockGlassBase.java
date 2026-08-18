package ruiseki.okcore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.Material;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

public class BlockGlassBase extends BlockGlass
    implements IBlockPropertyProvider, IBlockGui, IBlockStateAction, IBlockTooltipProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    protected boolean hasGui = false;

    /**
     * Make a new blockState instance.
     *
     * @param material         Material of this blockState.
     * @param ignoreSimilarity Whether neighbor blocks of the same type should connect/render seamlessly.
     */
    public BlockGlassBase(Material material, boolean ignoreSimilarity) {
        super(material, ignoreSimilarity);
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }
}

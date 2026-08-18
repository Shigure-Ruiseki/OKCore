package ruiseki.okcore.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;

public class BlockBase extends Block
    implements IBlockPropertyProvider, IBlockGui, IBlockStateAction, IBlockTooltipProvider {

    protected boolean hasGui = false;

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    /**
     * Make a new blockState instance.
     *
     * @param material Material of this blockState.
     */
    public BlockBase(Material material) {
        super(material);
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }
}

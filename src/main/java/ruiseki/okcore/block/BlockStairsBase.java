package ruiseki.okcore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;

public class BlockStairsBase extends BlockStairs
    implements IBlockPropertyProvider, IBlockGui, IBlockStateAction, IBlockTooltipProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    protected boolean hasGui = false;

    public BlockStairsBase(Block block) {
        super(block, 0);
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }
}

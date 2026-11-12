package ruiseki.okcore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.item.IInformationProvider;

public class BlockStairsBase extends BlockStairs
    implements IBlockPropertyProvider, IBlockGui, IBlockStateNative, IInformationProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    public BlockStairsBase(Block block) {
        super(block, 0);
    }
}

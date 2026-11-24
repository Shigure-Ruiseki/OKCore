package ruiseki.okcore.block.state;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockBlockStateTest extends Block implements IBlockStateNative {

    private final BlockStateContainer stateContainer;
    private IBlockState defaultBlockState;

    protected BlockBlockStateTest() {
        super(Material.rock);

        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
        this.createBlockStateDefinition(builder);
        this.stateContainer = builder.build();
        this.registerDefaultState(this.stateContainer.getBaseState());
    }

    @Override
    public BlockStateContainer getStateContainer() {
        return this.stateContainer;
    }

    @Override
    public void registerDefaultState(IBlockState state) {
        this.defaultBlockState = state;
    }

    @Override
    public IBlockState getDefaultState() {
        return this.defaultBlockState;
    }

    @Override
    public Block getBlockFromState(IBlockState state) {
        return this;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
}

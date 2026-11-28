package ruiseki.okcore.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.block.state.BlockStateContainer;
import ruiseki.okcore.block.state.IBlockState;
import ruiseki.okcore.block.state.IBlockStateNative;
import ruiseki.okcore.block.state.property.unlisted.MetaProperty;
import ruiseki.okcore.item.IInformationProvider;

public class BlockBase extends Block
    implements IBlockPropertyProvider, IBlockGui, IBlockStateNative, IInformationProvider {

    private final BlockStateContainer stateContainer;
    private IBlockState defaultBlockState;

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    /**
     * Make a new blockState instance.
     *
     * @param material Material of this blockState.
     */
    public BlockBase(Material material) {
        super(material);

        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
        this.createBlockStateDefinition(builder);
        this.stateContainer = builder.build();
        this.registerDefaultState(this.stateContainer.getBaseState());
    }

    @Override
    public void createBlockStateDefinition(BlockStateContainer.Builder builder) {
        builder.add(MetaProperty.INSTANCE);
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
}

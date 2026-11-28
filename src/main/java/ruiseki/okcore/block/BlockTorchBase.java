package ruiseki.okcore.block;

import net.minecraft.block.BlockTorch;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.block.state.BlockStateContainer;
import ruiseki.okcore.block.state.IBlockState;
import ruiseki.okcore.block.state.IBlockStateNative;
import ruiseki.okcore.block.state.property.unlisted.MetaProperty;
import ruiseki.okcore.item.IInformationProvider;

/**
 * Torch blockState that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public class BlockTorchBase extends BlockTorch
    implements IBlockPropertyProvider, IBlockGui, IBlockStateNative, IInformationProvider {

    private final BlockStateContainer stateContainer;
    private IBlockState defaultBlockState;

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    /**
     * Make a new blockState instance.
     *
     */
    public BlockTorchBase() {
        this.setHardness(0.0F);
        this.setLightLevel(0.9375F);
        this.setStepSound(soundTypeWood);

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

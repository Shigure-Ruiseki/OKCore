package ruiseki.okcore.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import ruiseki.okcore.block.state.BlockStateContainer;
import ruiseki.okcore.block.state.IBlockState;
import ruiseki.okcore.block.state.IBlockStateNative;
import ruiseki.okcore.block.state.property.listed.BooleanProperty;
import ruiseki.okcore.block.state.property.listed.IntegerProperty;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockStateHelpers;

public class BlockBlockStateTest extends Block implements IBlockStateNative {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 15);

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
    public void createBlockStateDefinition(BlockStateContainer.Builder builder) {
        builder.add(ACTIVE, LEVEL);
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

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        IBlockState state = BlockStateHelpers.getBlockState(worldIn, new BlockPos(x, y, z));
        if (state != null) {
            player.addChatComponentMessage(new ChatComponentText(state.toString()));
        }
        return super.onBlockActivated(worldIn, x, y, z, player, side, subX, subY, subZ);
    }
}

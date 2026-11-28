package ruiseki.okcore.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.client.model.color.IBlockColor;

import ruiseki.okcore.block.state.BlockStateContainer;
import ruiseki.okcore.block.state.IBlockState;
import ruiseki.okcore.block.state.IBlockStateNative;
import ruiseki.okcore.block.state.property.listed.DirectionProperty;
import ruiseki.okcore.block.state.property.listed.EnumProperty;
import ruiseki.okcore.block.state.property.unlisted.MetaProperty;
import ruiseki.okcore.client.model.IBlockStateModel;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.enums.EnumDye;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.DirectionHelpers;
import ruiseki.okcore.helper.ItemHelpers;

public class BlockBlockStateTest extends Block implements IBlockStateNative, IBlockStateModel, IBlockColor {

    public static final EnumProperty<EnumDye> COLOR = EnumProperty.create("color", EnumDye.class);
    public static final DirectionProperty FACING = DirectionProperty.create();

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
        builder.add(COLOR, FACING, MetaProperty.INSTANCE);
    }

    @Override
    public BlockStateContainer getStateContainer() {
        return this.stateContainer;
    }

    @Override
    public void registerDefaultState(IBlockState state) {
        this.defaultBlockState = state.withProperty(COLOR, EnumDye.WHITE)
            .withProperty(FACING, ForgeDirection.NORTH);
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
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        IBlockState state = BlockStateHelpers.getBlockState(worldIn, x, y, z);
        if (state != null) {
            player.addChatComponentMessage(new ChatComponentText(state.toString()));
        }
        return super.onBlockActivated(worldIn, x, y, z, player, side, subX, subY, subZ);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, ForgeDirection facing, float hitX, float hitY,
        float hitZ, int meta, EntityLivingBase placer) {
        return IBlockStateNative.super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer)
            .withProperty(FACING, DirectionHelpers.yawToDirection4(placer));
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z, int tintIndex) {
        if (world == null) return 0xFFFFFF;
        IBlockState state = BlockStateHelpers.getBlockState(world, x, y, z);
        if (state == null) return 0xFFFFFF;
        return state.getValue(COLOR)
            .dyeToRgb();
    }

    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        if (ItemHelpers.isEmpty(stack)) return 0xFFFFFF;
        IBlockState state = BlockStateHelpers.getBlockState(stack);
        if (state == null) return 0xFFFFFF;
        return state.getValue(COLOR)
            .dyeToRgb();
    }
}

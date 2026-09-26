package ruiseki.okcore.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blocks.util.BFSLeafDecay;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.helper.BlockHelpers;

/**
 * Block that extends from BlockLeaves that can hold ExtendedConfigs
 *
 * @author rubensworks
 */
public abstract class BlockLeavesBase extends BlockLeaves
    implements IBlockPropertyProvider, IBlockGui, IBlockStateNative, IBlockTooltipProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    /**
     * Make a new block instance.
     */
    public BlockLeavesBase() {
        super();
    }

    @Override
    public abstract Item getItemDropped(int meta, Random random, int fortune);

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune) {
        if (!world.isRemote) {
            ArrayList<ItemStack> items = getDrops(world, x, y, z, meta, fortune);

            for (ItemStack item : items) {
                if (world.rand.nextFloat() <= chance) {
                    this.dropBlockAsItem(world, x, y, z, item);
                }
            }
        }
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.field_150129_M[0] = new IIcon[] { reg.registerIcon(getTextureName() + "_fancy") };
        this.field_150129_M[1] = new IIcon[] { reg.registerIcon(getTextureName() + "_fast") };
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        int graphicsIndex = isOpaqueCube() ? 1 : this.field_150127_b;
        return this.field_150129_M[graphicsIndex][0];
    }

    @Override
    public String[] func_150125_e() {
        return new String[] { unlocalizedName };
    }

    @Override
    public boolean isOpaqueCube() { // OptiFine compat
        return Blocks.leaves.isOpaqueCube();
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) { // OptiFine compat
        return Blocks.leaves.shouldSideBeRendered(worldIn, x, y, z, side);
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 30;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 60;
    }

    public int getRange(int meta) {
        return 4;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        if (!BlockHelpers.isValidCreativeTab(this, tab)) return;
        list.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public boolean isLeaves(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(new ItemStack(this, 1, world.getBlockMetadata(x, y, z) & 3));
        return ret;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return new ItemStack(this, 1, world.getBlockMetadata(x, y, z) & 3);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) {
            final int meta = worldIn.getBlockMetadata(x, y, z);
            if ((meta & 8) != 0 && (meta & 4) == 0) {
                final int decayRange = getRange(meta % 4);
                BFSLeafDecay.handleDecayChecked(this, worldIn, x, y, z, meta, decayRange);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return getRenderColor(worldIn.getBlockMetadata(x, y, z));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderColor(int meta) {
        return 0xFFFFFF;
    }
}

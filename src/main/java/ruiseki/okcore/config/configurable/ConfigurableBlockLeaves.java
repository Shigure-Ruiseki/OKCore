package ruiseki.okcore.config.configurable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockHelpers;

/**
 * Block that extends from BlockLeaves that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public abstract class ConfigurableBlockLeaves extends BlockLeaves
    implements IConfigurableBlock, IBlockPropertyProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    @SuppressWarnings("rawtypes")
    protected ExtendedConfig eConfig = null;
    protected boolean hasGui = false;

    private int[] field_150128_a;

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    @SuppressWarnings("rawtypes")
    public ConfigurableBlockLeaves(ExtendedConfig eConfig) {
        this.setConfig(eConfig);
        this.setBlockName(eConfig.getUnlocalizedName());
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }

    private void setConfig(@SuppressWarnings("rawtypes") ExtendedConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return eConfig;
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
    public boolean isOpaqueCube() {
        return Blocks.leaves.isOpaqueCube();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) { // OptiFine compat
        return Blocks.leaves.shouldSideBeRendered(worldIn, x, y, z, side);
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
    public ItemStack getPickBlock(net.minecraft.util.MovingObjectPosition target, World world, int x, int y, int z,
        EntityPlayer player) {
        return new ItemStack(this, 1, world.getBlockMetadata(x, y, z) & 3);
    }

    @Override
    public String[] func_150125_e() {
        return new String[] { eConfig.getNamedId() };
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return field_150129_M[field_150121_P ? 1 : 0][(meta % 4)];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.field_150129_M[0] = new IIcon[1];
        this.field_150129_M[1] = new IIcon[1];

        this.field_150129_M[0][0] = iconRegister.registerIcon(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId());
        this.field_150129_M[1][0] = iconRegister.registerIcon(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId()
                + "_opaque");
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

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) {
            int l = worldIn.getBlockMetadata(x, y, z);
            int decayRange = getRange(l % 4);

            if ((l & 8) != 0 && (l & 4) == 0) {
                int i1 = decayRange + 1;
                byte b1 = 32;
                int j1 = b1 * b1;
                int k1 = b1 / 2;

                if (this.field_150128_a == null) {
                    this.field_150128_a = new int[b1 * b1 * b1];
                }

                int l1;

                if (worldIn.checkChunksExist(x - i1, y - i1, z - i1, x + i1, y + i1, z + i1)) {
                    int i2;
                    int j2;

                    for (l1 = -decayRange; l1 <= decayRange; ++l1) {
                        for (i2 = -decayRange; i2 <= decayRange; ++i2) {
                            for (j2 = -decayRange; j2 <= decayRange; ++j2) {
                                Block block = worldIn.getBlock(x + l1, y + i2, z + j2);

                                int i = (l1 + k1) * j1 + (i2 + k1) * b1 + j2 + k1;
                                if (!block.canSustainLeaves(worldIn, x + l1, y + i2, z + j2)) {
                                    if (block.isLeaves(worldIn, x + l1, y + i2, z + j2)) {
                                        this.field_150128_a[i] = -2;
                                    } else {
                                        this.field_150128_a[i] = -1;
                                    }
                                } else {
                                    this.field_150128_a[i] = 0;
                                }
                            }
                        }
                    }

                    for (l1 = 1; l1 <= decayRange; ++l1) {
                        for (i2 = -decayRange; i2 <= decayRange; ++i2) {
                            for (j2 = -decayRange; j2 <= decayRange; ++j2) {
                                for (int k2 = -decayRange; k2 <= decayRange; ++k2) {
                                    if (this.field_150128_a[(i2 + k1) * j1 + (j2 + k1) * b1 + k2 + k1] == l1 - 1) {
                                        int i = (i2 + k1 - 1) * j1 + (j2 + k1) * b1 + k2 + k1;
                                        if (this.field_150128_a[i] == -2) {
                                            this.field_150128_a[i] = l1;
                                        }

                                        int i3 = (i2 + k1 + 1) * j1 + (j2 + k1) * b1 + k2 + k1;
                                        if (this.field_150128_a[i3] == -2) {
                                            this.field_150128_a[i3] = l1;
                                        }

                                        int i4 = (i2 + k1) * j1 + (j2 + k1 - 1) * b1 + k2 + k1;
                                        if (this.field_150128_a[i4] == -2) {
                                            this.field_150128_a[i4] = l1;
                                        }

                                        int i5 = (i2 + k1) * j1 + (j2 + k1 + 1) * b1 + k2 + k1;
                                        if (this.field_150128_a[i5] == -2) {
                                            this.field_150128_a[i5] = l1;
                                        }

                                        int i6 = (i2 + k1) * j1 + (j2 + k1) * b1 + (k2 + k1 - 1);
                                        if (this.field_150128_a[i6] == -2) {
                                            this.field_150128_a[i6] = l1;
                                        }

                                        int i7 = (i2 + k1) * j1 + (j2 + k1) * b1 + k2 + k1 + 1;
                                        if (this.field_150128_a[i7] == -2) {
                                            this.field_150128_a[i7] = l1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                l1 = this.field_150128_a[k1 * j1 + k1 * b1 + k1];

                if (l1 >= 0) {
                    worldIn.setBlockMetadataWithNotify(x, y, z, l & -9, 4);
                } else {
                    this.removeLeaves(worldIn, x, y, z);
                }
            }
        }
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return getRenderColor(worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    public int getRenderColor(int meta) {
        return 16777215;
    }

    public MovingObjectPosition rayTrace(BlockPos pos, Vec3 start, Vec3 end, AxisAlignedBB boundingBox) {
        Vec3 vecStart = start.addVector(-pos.getX(), -pos.getY(), -pos.getZ());
        Vec3 vecEnd = end.addVector(-pos.getX(), -pos.getY(), -pos.getZ());
        MovingObjectPosition intercept = boundingBox.calculateIntercept(vecStart, vecEnd);
        return intercept != null ? new MovingObjectPosition(
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            intercept.sideHit,
            intercept.hitVec.addVector(pos.getX(), pos.getY(), pos.getZ())) : null;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World worldIn, int x, int y, int z, Vec3 start, Vec3 endVec) {
        return this.rayTrace(new BlockPos(x, y, z), start, endVec, getSelectedBoundingBoxFromPool(worldIn, x, y, z));
    }
}

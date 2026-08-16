package ruiseki.okcore.config.configurable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.BooleanProperty;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.helper.BlockHelpers;
import ruiseki.okcore.helper.BlockStateHelpers;

/**
 * Block that extends from BlockLeaves that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public abstract class ConfigurableBlockLeaves extends BlockLeaves implements IConfigurableBlock {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    @BlockProperty
    public static final BooleanProperty CHECK_DECAY = BooleanProperty.construct(
        "check_decay",
        true,
        (world, x, y, z) -> (world.getBlockMetadata(x, y, z) & 8) != 0,
        (world, x, y, z, value) -> {
            int meta = world.getBlockMetadata(x, y, z);
            int newMeta = value ? (meta | 8) : (meta & ~8);
            world.setBlockMetadataWithNotify(x, y, z, newMeta, 4);
        });

    @BlockProperty
    public static final BooleanProperty DECAYABLE = BooleanProperty.construct(
        "decayable",
        true,
        (world, x, y, z) -> (world.getBlockMetadata(x, y, z) & 4) == 0,
        (world, x, y, z, value) -> {
            int meta = world.getBlockMetadata(x, y, z);
            int newMeta = value ? (meta & ~4) : (meta | 4);
            world.setBlockMetadataWithNotify(x, y, z, newMeta, 4);
        });

    protected BlockConfig eConfig = null;
    protected boolean hasGui = false;

    private int[] surroundings;

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public ConfigurableBlockLeaves(ExtendedConfig<BlockConfig> eConfig) {
        this.setConfig((BlockConfig) eConfig);
        this.setBlockName(eConfig.getUnlocalizedName());
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }

    private void setConfig(BlockConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public BlockConfig getConfig() {
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

    @Override
    public IIcon getIcon(int side, int meta) {
        return null;
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

            if (BlockStateHelpers.get(worldIn, x, y, z, CHECK_DECAY)
                && BlockStateHelpers.get(worldIn, x, y, z, DECAYABLE)) {
                int i1 = decayRange + 1;
                byte b1 = 32;
                int j1 = b1 * b1;
                int k1 = b1 / 2;

                if (this.surroundings == null) {
                    this.surroundings = new int[b1 * b1 * b1];
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
                                        this.surroundings[i] = -2;
                                    } else {
                                        this.surroundings[i] = -1;
                                    }
                                } else {
                                    this.surroundings[i] = 0;
                                }
                            }
                        }
                    }

                    for (l1 = 1; l1 <= decayRange; ++l1) {
                        for (i2 = -decayRange; i2 <= decayRange; ++i2) {
                            for (j2 = -decayRange; j2 <= decayRange; ++j2) {
                                for (int k2 = -decayRange; k2 <= decayRange; ++k2) {
                                    if (this.surroundings[(i2 + k1) * j1 + (j2 + k1) * b1 + k2 + k1] == l1 - 1) {
                                        int i = (i2 + k1 - 1) * j1 + (j2 + k1) * b1 + k2 + k1;
                                        if (this.surroundings[i] == -2) {
                                            this.surroundings[i] = l1;
                                        }

                                        int i3 = (i2 + k1 + 1) * j1 + (j2 + k1) * b1 + k2 + k1;
                                        if (this.surroundings[i3] == -2) {
                                            this.surroundings[i3] = l1;
                                        }

                                        int i4 = (i2 + k1) * j1 + (j2 + k1 - 1) * b1 + k2 + k1;
                                        if (this.surroundings[i4] == -2) {
                                            this.surroundings[i4] = l1;
                                        }

                                        int i5 = (i2 + k1) * j1 + (j2 + k1 + 1) * b1 + k2 + k1;
                                        if (this.surroundings[i5] == -2) {
                                            this.surroundings[i5] = l1;
                                        }

                                        int i6 = (i2 + k1) * j1 + (j2 + k1) * b1 + (k2 + k1 - 1);
                                        if (this.surroundings[i6] == -2) {
                                            this.surroundings[i6] = l1;
                                        }

                                        int i7 = (i2 + k1) * j1 + (j2 + k1) * b1 + k2 + k1 + 1;
                                        if (this.surroundings[i7] == -2) {
                                            this.surroundings[i7] = l1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                l1 = this.surroundings[k1 * j1 + k1 * b1 + k1];

                if (l1 >= 0) {
                    BlockStateHelpers.set(worldIn, x, y, z, CHECK_DECAY, false);
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
}

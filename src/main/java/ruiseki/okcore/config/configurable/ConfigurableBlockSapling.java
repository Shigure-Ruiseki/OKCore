package ruiseki.okcore.config.configurable;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.world.gen.WorldGeneratorTree;

public class ConfigurableBlockSapling extends BlockSapling implements IConfigurableBlock, IBlockPropertyProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    @SuppressWarnings("rawtypes")
    protected ExtendedConfig eConfig = null;
    protected boolean hasGui = false;

    @SideOnly(Side.CLIENT)
    private IIcon blockIcon;

    private WorldGeneratorTree treeGenerator;

    /**
     * Make a new blockState instance.
     *
     * @param eConfig       Config for this blockState.
     * @param material      Material of this blockState.
     * @param treeGenerator The world generator of the tree.
     */
    @SuppressWarnings({ "rawtypes" })
    public ConfigurableBlockSapling(ExtendedConfig eConfig, Material material, WorldGeneratorTree treeGenerator) {
        this.setConfig(eConfig);
        this.setBlockName(eConfig.getUnlocalizedName());
        this.setBlockTextureName(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId());
        this.treeGenerator = treeGenerator;
        setStepSound(soundTypeGrass);
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }

    @SuppressWarnings("rawtypes")
    private void setConfig(ExtendedConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return eConfig;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
    }

    @Override
    public void func_149879_c(World world, int x, int y, int z, Random rand) {
        if (!TerrainGen.saplingGrowTree(world, rand, x, y, z)) return;
        if (world.isRemote) {
            return;
        }

        world.setBlockToAir(x, y, z);

        if (!treeGenerator.growTree(world, rand, x, y, z)) {
            world.setBlock(x, y, z, this, 0, 4);
        }
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.blockIcon;
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

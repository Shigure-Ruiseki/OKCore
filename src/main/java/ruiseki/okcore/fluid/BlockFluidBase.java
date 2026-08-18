package ruiseki.okcore.fluid;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.Delegate;
import ruiseki.okcore.block.IBlockGui;
import ruiseki.okcore.block.IBlockStateAction;
import ruiseki.okcore.block.IEntityDropParticleFXBlock;
import ruiseki.okcore.block.ParticleDropBlockComponent;
import ruiseki.okcore.block.collidable.ImmutableAxisAlignedBB;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;

/**
 * Block that represents an in-world fluid that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public abstract class BlockFluidBase extends BlockFluidClassic
    implements IBlockPropertyProvider, IBlockGui, IBlockStateAction, IEntityDropParticleFXBlock {

    private Fluid fluid;

    protected boolean hasGui = false;

    @SideOnly(Side.CLIENT)
    protected ParticleDropBlockComponent particleDropBlockComponent;

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    /**
     * Make a new blockState instance.
     *
     * @param fluid    The fluid this blockState has to represent
     * @param material Material of this blockState.
     */
    public BlockFluidBase(Fluid fluid, Material material) {
        super(fluid, material);
        fluid.setBlock(this);
        this.fluid = fluid;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return ImmutableAxisAlignedBB.NULL_AABB;
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }

    /**
     * @return The associated fluid.
     */
    public Fluid getFluid() {
        return this.fluid;
    }

    /**
     * Set the drop particle color.
     *
     * @param particleRed   Red color.
     * @param particleGreen Green color.
     * @param particleBlue  Blue color.
     * @return This instance of the blockState.
     */
    @SideOnly(Side.CLIENT)
    public BlockFluidBase setParticleColor(float particleRed, float particleGreen, float particleBlue) {
        particleDropBlockComponent = new ParticleDropBlockComponent(particleRed, particleGreen, particleBlue);
        return this;
    }

    /**
     * For the particle effects underneath a blockState that has the liquid on top.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        super.randomDisplayTick(worldIn, x, y, z, random);
        if (particleDropBlockComponent != null) particleDropBlockComponent.randomDisplayTick(worldIn, x, y, z, random);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side <= 1 ? this.fluid.getStillIcon() : this.fluid.getFlowingIcon();
    }
}

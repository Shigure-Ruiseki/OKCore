package ruiseki.okcore.config.configurable;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.Delegate;
import ruiseki.okcore.block.IEntityDropParticleFXBlock;
import ruiseki.okcore.block.ParticleDropBlockComponent;
import ruiseki.okcore.block.collidable.ImmutableAxisAlignedBB;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * Block that represents an in-world fluid that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public abstract class ConfigurableBlockFluidClassic extends BlockFluidClassic
    implements IConfigurableBlock, IEntityDropParticleFXBlock {

    private Fluid fluid;

    protected BlockConfig eConfig = null;
    protected boolean hasGui = false;

    @SideOnly(Side.CLIENT)
    protected ParticleDropBlockComponent particleDropBlockComponent;

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    /**
     * Make a new blockState instance.
     *
     * @param eConfig  Config for this blockState.
     * @param fluid    The fluid this blockState has to represent
     * @param material Material of this blockState.
     */
    public ConfigurableBlockFluidClassic(ExtendedConfig<BlockConfig> eConfig, Fluid fluid, Material material) {
        super(fluid, material);
        this.setConfig((BlockConfig) eConfig);
        this.setBlockName(eConfig.getUnlocalizedName());
        this.setBlockTextureName(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId());
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

    private void setConfig(BlockConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public BlockConfig getConfig() {
        return eConfig;
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
    public ConfigurableBlockFluidClassic setParticleColor(float particleRed, float particleGreen, float particleBlue) {
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
}

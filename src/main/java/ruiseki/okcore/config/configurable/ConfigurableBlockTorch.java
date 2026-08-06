package ruiseki.okcore.config.configurable;

import net.minecraft.block.BlockTorch;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * Torch blockState that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public class ConfigurableBlockTorch extends BlockTorch implements IConfigurableBlock, IBlockPropertyProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    @SuppressWarnings("rawtypes")
    protected ExtendedConfig eConfig = null;
    protected boolean hasGui = false;

    /**
     * Make a new blockState instance.
     *
     * @param eConfig Config for this blockState.
     */
    @SuppressWarnings({ "rawtypes" })
    public ConfigurableBlockTorch(ExtendedConfig eConfig) {
        this.setConfig(eConfig);
        this.setBlockName(eConfig.getUnlocalizedName());
        this.setBlockTextureName(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId());
        this.setHardness(0.0F);
        this.setLightLevel(0.9375F);
        this.setStepSound(soundTypeWood);
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

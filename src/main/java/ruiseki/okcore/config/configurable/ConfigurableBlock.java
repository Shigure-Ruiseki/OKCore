package ruiseki.okcore.config.configurable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;

public class ConfigurableBlock extends Block implements IConfigurableBlock, IBlockPropertyProvider {

    @SuppressWarnings("rawtypes")
    protected BlockConfig eConfig = null;
    protected boolean hasGui = false;

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    /**
     * Make a new blockState instance.
     *
     * @param eConfig  Config for this blockState.
     * @param material Material of this blockState.
     */
    @SuppressWarnings({ "rawtypes" })
    public ConfigurableBlock(ExtendedConfig eConfig, Material material) {
        super(material);
        this.setConfig(eConfig);
        this.setBlockName(eConfig.getUnlocalizedName());
        this.setBlockTextureName(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId());
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }

    private void setConfig(@SuppressWarnings("rawtypes") ExtendedConfig eConfig) {
        this.eConfig = (BlockConfig) eConfig;
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

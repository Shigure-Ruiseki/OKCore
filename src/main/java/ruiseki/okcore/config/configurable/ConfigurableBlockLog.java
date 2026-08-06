package ruiseki.okcore.config.configurable;

import java.util.List;

import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockHelpers;

public class ConfigurableBlockLog extends BlockLog implements IConfigurableBlock, IBlockPropertyProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    @SuppressWarnings("rawtypes")
    protected ExtendedConfig eConfig = null;
    protected boolean hasGui = false;

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    @SuppressWarnings("rawtypes")
    public ConfigurableBlockLog(ExtendedConfig eConfig) {
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        if (!BlockHelpers.isValidCreativeTab(this, tab)) return;
        list.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.field_150167_a = new IIcon[1];
        this.field_150166_b = new IIcon[1];
        this.field_150167_a = new IIcon[] { reg.registerIcon(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId()) };
        this.field_150166_b = new IIcon[] { reg.registerIcon(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId()
                + "_top") };
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

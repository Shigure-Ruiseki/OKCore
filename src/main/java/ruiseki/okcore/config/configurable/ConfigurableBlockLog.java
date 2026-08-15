package ruiseki.okcore.config.configurable;

import java.util.List;

import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.geometry.Axis;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.block.property.IEnumProperty;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockHelpers;

public class ConfigurableBlockLog extends BlockLog implements IConfigurableBlock, IBlockPropertyProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    // For ItemBlock
    @BlockProperty(allowBlock = false)
    private static final IEnumProperty<Axis> AXIS = IEnumProperty.construct("axis", Axis.class, Axis.Y);

    protected BlockConfig eConfig = null;
    protected boolean hasGui = false;

    public ConfigurableBlockLog(ExtendedConfig<BlockConfig> eConfig) {
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        if (!BlockHelpers.isValidCreativeTab(this, tab)) return;
        list.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {

    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ,
        int meta) {
        return meta;
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, ForgeDirection facing, float hitX, float hitY,
        float hitZ, int meta, EntityLivingBase placer) {
        BlockState state = IConfigurableBlock.super.getStateForPlacement(
            world,
            pos,
            facing,
            hitX,
            hitY,
            hitZ,
            meta,
            placer);
        state.setPropertyValue("axis", Axis.fromDirection(facing));
        return state;
    }
}

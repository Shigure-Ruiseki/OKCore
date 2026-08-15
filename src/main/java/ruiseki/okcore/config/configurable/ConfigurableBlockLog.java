package ruiseki.okcore.config.configurable;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.geometry.Axis;

import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.IEnumProperty;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockHelpers;

public class ConfigurableBlockLog extends ConfigurableBlock {
    @BlockProperty
    private static final IEnumProperty<Axis> AXIS = IEnumProperty
        .construct("axis", Axis.class, Axis.Y, (world, x, y, z) -> {
            int meta = world.getBlockMetadata(x, y, z);
            return switch (meta & 0b1100) {
                case 0b0100 -> Axis.X;
                case 0b1000 -> Axis.Z;
                default -> Axis.Y;
            };
        }, (world, x, y, z, value) -> {
            int currentMeta = world.getBlockMetadata(x, y, z);
            int axisBits = switch (value) {
                case X -> 0b0100;
                case Z -> 0b1000;
                default -> 0b0000; // Axis.Y
            };
            int newMeta = (currentMeta & ~0b1100) | axisBits;
            world.setBlockMetadataWithNotify(x, y, z, newMeta, 3);
        });

    protected BlockConfig eConfig = null;
    protected boolean hasGui = false;

    public ConfigurableBlockLog(ExtendedConfig<BlockConfig> eConfig) {
        super(eConfig, Material.wood);
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        if (!BlockHelpers.isValidCreativeTab(this, tab)) return;
        list.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, ForgeDirection facing, float hitX, float hitY,
        float hitZ, int meta, EntityLivingBase placer) {
        BlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
        state.setPropertyValue("axis", Axis.fromDirection(facing));
        return state;
    }
}

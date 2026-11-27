package ruiseki.okcore.block.state.property.unlisted;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import ruiseki.okcore.datastructure.BlockPos;

public class MetaProperty extends IntegerUnlistedProperty {

    public static final MetaProperty INSTANCE = new MetaProperty();

    private MetaProperty() {
        super("meta", 0);
    }

    @Override
    public Integer getValue(IBlockAccess world, BlockPos pos) {
        return pos.getBlockMetadata(world);
    }

    @Override
    public void setValue(World world, BlockPos pos, Integer value) {
        pos.setBlockMetadata(world, value, 2);
    }

    @Override
    public Integer getValue(ItemStack stack, IBlockAccess world, EntityLivingBase livingBase) {
        return stack.getItemDamage();
    }

    @Override
    public void setValue(ItemStack stack, World world, EntityLivingBase livingBase, Integer value) {
        stack.setItemDamage(value);
    }
}

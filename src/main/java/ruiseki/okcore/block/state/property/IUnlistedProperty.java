package ruiseki.okcore.block.state.property;

import java.util.Optional;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import ruiseki.okcore.datastructure.BlockPos;

public interface IUnlistedProperty<T extends Comparable<T>> extends IProperty<T> {

    Optional<T> parseValue(String value);

    // Block context
    T getValue(IBlockAccess world, BlockPos pos);

    void setValue(World world, BlockPos pos, T value);

    // Item context
    T getValue(ItemStack stack, IBlockAccess world, EntityLivingBase livingBase);

    void setValue(ItemStack stack, World world, EntityLivingBase livingBase, T value);
}

package ruiseki.okcore.block.state;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableMap;

import ruiseki.okcore.block.state.property.IListedProperty;
import ruiseki.okcore.block.state.property.IProperty;
import ruiseki.okcore.block.state.property.IUnlistedProperty;
import ruiseki.okcore.datastructure.BlockPos;

public interface IBlockState {

    // Property access
    <T extends Comparable<T>> T getValue(IProperty<T> property);

    <T extends Comparable<T>> IBlockState withProperty(IListedProperty<T> property, T value);

    <T extends Comparable<T>> IBlockState withProperty(IUnlistedProperty<T> property, T value);

    // Property definitions
    ImmutableMap<IProperty<?>, Comparable<?>> getProperties();

    Collection<IProperty<?>> getPropertyKeys();

    Collection<IUnlistedProperty<?>> getUnlistedProperties();

    // Block information
    Block getBlock();

    // Read / Write state from world
    IBlockState resolveUnlistedProperties(IBlockAccess world, BlockPos pos);

    boolean setBlockState(World world, BlockPos pos);

    NBTTagCompound serializeNBT();

    IBlockState deserializeNBT(NBTTagCompound nbt);
}

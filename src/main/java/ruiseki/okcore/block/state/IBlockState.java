package ruiseki.okcore.block.state;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;

import ruiseki.okcore.block.state.property.IListedProperty;
import ruiseki.okcore.block.state.property.IProperty;
import ruiseki.okcore.block.state.property.IUnlistedProperty;
import ruiseki.okcore.datastructure.BlockPos;

public interface IBlockState {

    // Listed Properties

    <T extends Comparable<T>> T getValue(IListedProperty<T> property);

    <T extends Comparable<T>, V extends T> IBlockState withProperty(IListedProperty<T> property, V value);

    // Unlisted Properties

    Collection<IUnlistedProperty<?>> getUnlistedNames();

    <T extends Comparable<T>> T getValue(IUnlistedProperty<T> property);

    <T extends Comparable<T>> IBlockState withProperty(IUnlistedProperty<T> property, T value);

    ImmutableMap<IUnlistedProperty<?>, Optional<?>> getUnlistedProperties();

    IBlockState getClean();

    IBlockState resolveUnlistedProperties(IBlockAccess world, BlockPos pos);

    // Property Definitions

    ImmutableMap<IListedProperty<?>, Comparable<?>> getProperties();

    ImmutableTable<IListedProperty<?>, Comparable<?>, IBlockState> getPropertyValueTable();

    Collection<IProperty<?>> getPropertyKeys();

    // Block Information

    Block getBlock();

    // World Operations
    boolean setBlockState(World world, BlockPos pos);

    boolean setBlockState(World world, BlockPos pos, int flags);

    Map<String, String> toMap();
}

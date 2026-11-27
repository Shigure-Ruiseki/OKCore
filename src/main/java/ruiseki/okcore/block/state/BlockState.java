
package ruiseki.okcore.block.state;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import ruiseki.okcore.block.state.property.IListedProperty;
import ruiseki.okcore.block.state.property.IProperty;
import ruiseki.okcore.block.state.property.IUnlistedProperty;
import ruiseki.okcore.block.state.property.unlisted.MetaProperty;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.EBSHelpers;
import ruiseki.okcore.helper.Helpers;

public class BlockState implements IBlockState {

    private final Block block;
    private final ImmutableMap<IListedProperty<?>, Comparable<?>> properties;
    protected ImmutableTable<IListedProperty<?>, Comparable<?>, IBlockState> propertyValueTable;
    private final ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties;
    private final IBlockState cleanState;

    protected BlockState(Block blockIn, ImmutableMap<IListedProperty<?>, Comparable<?>> propertiesIn,
        ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
        this(blockIn, propertiesIn, unlistedProperties, null, null);
    }

    protected BlockState(Block blockIn, ImmutableMap<IListedProperty<?>, Comparable<?>> propertiesIn,
        ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties,
        ImmutableTable<IListedProperty<?>, Comparable<?>, IBlockState> propertyValueTable, IBlockState clean) {
        this.block = blockIn;
        this.properties = propertiesIn;
        this.propertyValueTable = propertyValueTable;
        this.unlistedProperties = unlistedProperties;
        this.cleanState = clean == null ? this : clean;
    }

    @Override
    public Collection<IProperty<?>> getPropertyKeys() {
        return Collections.unmodifiableCollection(this.properties.keySet());
    }

    @Override
    public <T extends Comparable<T>> T getValue(IListedProperty<T> property) {
        Comparable<?> comparable = this.properties.get(property);
        if (comparable == null) {
            throw new IllegalArgumentException(
                "Cannot get property " + property + " as it does not exist in " + this.block);
        } else {
            return property.getType()
                .cast(comparable);
        }
    }

    @Override
    public <T extends Comparable<T>, V extends T> IBlockState withProperty(IListedProperty<T> property, V value) {
        Comparable<?> comparable = this.properties.get(property);
        IBlockState clean = null;
        if (comparable == null) {
            throw new IllegalArgumentException(
                "Cannot set property " + property + " as it does not exist in " + this.block);
        } else if (comparable == value) {
            clean = this;
        } else {
            IBlockState iblockstate = this.propertyValueTable.get(property, value);
            if (iblockstate == null) {
                throw new IllegalArgumentException(
                    "Cannot set property " + property
                        + " to "
                        + value
                        + " on block "
                        + Helpers.getLocation(this.block)
                        + ", it is not an allowed value");
            } else {
                clean = iblockstate;
            }
        }

        if (clean == this.cleanState) {
            return this;
        }

        if (this == this.cleanState) { // no dynamic properties present, looking up in the normal table
            return clean;
        }

        return new BlockState(
            getBlock(),
            clean.getProperties(),
            unlistedProperties,
            clean.getPropertyValueTable(),
            this.cleanState);
    }

    @Override
    public ImmutableMap<IListedProperty<?>, Comparable<?>> getProperties() {
        return properties;
    }

    @Override
    public ImmutableTable<IListedProperty<?>, Comparable<?>, IBlockState> getPropertyValueTable() {
        return propertyValueTable;
    }

    @Override
    public @NotNull Block getBlock() {
        return this.block;
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        return this == p_equals_1_;
    }

    @Override
    public int hashCode() {
        return this.properties.hashCode();
    }

    public void buildPropertyValueTable(Map<Map<IListedProperty<?>, Comparable<?>>, BlockState> map) {
        if (this.propertyValueTable != null) {
            throw new IllegalStateException();
        } else {
            Table<IListedProperty<?>, Comparable<?>, IBlockState> table = HashBasedTable.create();
            for (Map.Entry<IListedProperty<?>, Comparable<?>> iListedPropertyComparableEntry : this.properties
                .entrySet()) {
                IListedProperty<?> iproperty = iListedPropertyComparableEntry.getKey();

                for (Comparable<?> comparable : iproperty.getAllowedValues()) {
                    if (comparable != iListedPropertyComparableEntry.getValue()) {
                        table.put(iproperty, comparable, map.get(this.getPropertiesWithValue(iproperty, comparable)));
                    }
                }
            }
            this.propertyValueTable = ImmutableTable.copyOf(table);
        }
    }

    private Map<IListedProperty<?>, Comparable<?>> getPropertiesWithValue(IListedProperty<?> property,
        Comparable<?> value) {
        Map<IListedProperty<?>, Comparable<?>> map = Maps.newHashMap(this.properties);
        map.put(property, value);
        return map;
    }

    @Override
    public <T extends Comparable<T>> IBlockState withProperty(IUnlistedProperty<T> property, T value) {
        Optional<?> oldValue = unlistedProperties.get(property);
        if (oldValue == null) {
            throw new IllegalArgumentException(
                "Cannot set unlisted property " + property + " as it does not exist in " + this.block);
        }
        if (Objects.equals(oldValue.orElse(null), value)) {
            return this;
        }
        if (!property.isValid(value)) {
            throw new IllegalArgumentException(
                "Cannot set unlisted property " + property
                    + " to "
                    + value
                    + " on block "
                    + Helpers.getLocation(this.block)
                    + ", it is not an allowed value");
        }
        boolean clean = true;
        ImmutableMap.Builder<IUnlistedProperty<?>, Optional<?>> builder = ImmutableMap.builder();
        for (Map.Entry<IUnlistedProperty<?>, Optional<?>> entry : unlistedProperties.entrySet()) {
            IUnlistedProperty<?> key = entry.getKey();
            Optional<?> newValue = key.equals(property) ? Optional.ofNullable(value) : entry.getValue();
            if (newValue.isPresent()) clean = false;
            builder.put(key, newValue);
        }
        if (clean) { // no dynamic properties, lookup normal state
            return cleanState;
        }
        return new BlockState(getBlock(), getProperties(), builder.build(), propertyValueTable, this.cleanState);
    }

    @Override
    public Collection<IUnlistedProperty<?>> getUnlistedNames() {
        return unlistedProperties.keySet();
    }

    @Override
    public <T extends Comparable<T>> T getValue(IUnlistedProperty<T> property) {
        Optional<?> value = unlistedProperties.get(property);
        if (value == null) {
            throw new IllegalArgumentException(
                "Cannot get unlisted property " + property + " as it does not exist in " + this.block);
        }
        return value.map(property.getType()::cast)
            .orElseGet(property::getDefaultValue);
    }

    @Override
    public IBlockState resolveUnlistedProperties(IBlockAccess world, BlockPos pos) {
        IBlockState state = this;
        for (IUnlistedProperty<?> property : this.unlistedProperties.keySet()) {
            state = resolveUnlistedProperty(state, world, pos, property);
        }
        return state;
    }

    private static <T extends Comparable<T>> IBlockState resolveUnlistedProperty(IBlockState state, IBlockAccess access,
        BlockPos pos, IUnlistedProperty<T> property) {
        T value = property.getValue(access, pos);
        return state.withProperty(property, value);
    }

    @Override
    public ImmutableMap<IUnlistedProperty<?>, Optional<?>> getUnlistedProperties() {
        return unlistedProperties;
    }

    @Override
    public IBlockState getClean() {
        return cleanState;
    }

    @Override
    public boolean setBlockState(World world, BlockPos pos) {
        return this.setBlockState(world, pos, 3);
    }

    @Override
    public boolean setBlockState(World world, BlockPos pos, int flags) {
        pos.setBlock(world, this.block, this.getValue(MetaProperty.INSTANCE), flags);
        Chunk chunk = pos.getChunk(world);

        ExtendedBlockStorage ebs = EBSHelpers.getEBS(world, pos);
        if (ebs == null) return false;

        BlockStateStorage storage = BlockStateStorage.getStorage(ebs, true);
        if (storage == null) return false;

        // Store the actual state, including its current Listed Values.
        storage.setBlockState(pos, this);

        // Apply Unlisted properties to the world.
        for (Map.Entry<IUnlistedProperty<?>, Optional<?>> entry : this.unlistedProperties.entrySet()) {
            Optional<?> optional = entry.getValue();
            if (optional.isEmpty()) continue;

            applyUnlistedProperty(world, pos, entry.getKey(), optional.get());
        }

        chunk.setChunkModified();
        return true;
    }

    @Override
    public Map<String, String> toMap() {
        return Map.of();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(Helpers.getLocation(this.block));

        if (!properties.isEmpty()) {
            builder.append('[');
            boolean first = true;

            for (Map.Entry<IListedProperty<?>, Comparable<?>> entry : properties.entrySet()) {
                if (!first) builder.append(',');
                first = false;

                builder.append(
                    entry.getKey()
                        .getName())
                    .append('=')
                    .append(getPropertyValueName(entry.getKey(), entry.getValue()));
            }

            builder.append(']');
        }

        if (!unlistedProperties.isEmpty()) {
            builder.append('{');
            boolean first = true;

            for (Map.Entry<IUnlistedProperty<?>, Optional<?>> entry : unlistedProperties.entrySet()) {
                if (!first) builder.append(',');
                first = false;

                builder.append(
                    entry.getKey()
                        .getName())
                    .append('=')
                    .append(
                        entry.getValue()
                            .map(Object::toString)
                            .orElse("null"));
            }

            builder.append('}');
        }

        return builder.toString();
    }

    private static <T extends Comparable<T>> String getPropertyValueName(IListedProperty<T> property,
        Comparable<?> value) {
        return property.getName(
            property.getType()
                .cast(value));
    }

    private static <T extends Comparable<T>> void applyUnlistedProperty(World world, BlockPos pos,
        IUnlistedProperty<T> property, Object value) {
        T valueType = property.getType()
            .cast(value);
        property.setValue(world, pos, valueType);
    }

}

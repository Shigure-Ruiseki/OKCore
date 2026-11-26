
package ruiseki.okcore.block.state;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import ruiseki.okcore.block.state.property.IListedProperty;
import ruiseki.okcore.block.state.property.IProperty;
import ruiseki.okcore.block.state.property.IUnlistedProperty;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.Helpers;

public class BlockState implements IBlockState {

    private final Block block;

    /**
     * Stores both listed and unlisted property values.
     */
    private final ImmutableMap<IProperty<?>, Comparable<?>> properties;

    /**
     * Valid unlisted property definitions.
     * This is metadata, not a storage of property values.
     */
    private final ImmutableSet<IUnlistedProperty<?>> unlistedProperties;

    /**
     * Transitions for listed properties only.
     * Shared with states having the same listed values.
     */
    protected ImmutableTable<IProperty<?>, Comparable<?>, IBlockState> propertyValueTable;

    protected BlockState(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties,
        ImmutableSet<IUnlistedProperty<?>> unlistedProperties) {
        this(block, properties, unlistedProperties, null);
    }

    protected BlockState(Block block, ImmutableMap<IProperty<?>, Comparable<?>> properties,
        ImmutableSet<IUnlistedProperty<?>> unlistedProperties,
        ImmutableTable<IProperty<?>, Comparable<?>, IBlockState> propertyValueTable) {
        this.block = Objects.requireNonNull(block, "block");
        this.properties = Objects.requireNonNull(properties, "properties");
        this.unlistedProperties = Objects.requireNonNull(unlistedProperties, "unlistedProperties");
        this.propertyValueTable = propertyValueTable;
    }

    @Override
    public ImmutableMap<IProperty<?>, Comparable<?>> getProperties() {
        return this.properties;
    }

    @Override
    public Collection<IProperty<?>> getPropertyKeys() {
        return this.properties.keySet();
    }

    @Override
    public Collection<IUnlistedProperty<?>> getUnlistedProperties() {
        return this.unlistedProperties;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    @Override
    public <T extends Comparable<T>> T getValue(IProperty<T> property) {
        Comparable<?> value = this.properties.get(property);

        if (value == null) {
            throw new IllegalArgumentException("Property " + property.getName() + " does not exist in this state");
        }

        return property.getType()
            .cast(value);
    }

    @Override
    public <T extends Comparable<T>> IBlockState withProperty(IListedProperty<T> property, T value) {
        validateProperty(property, value);

        Comparable<?> currentValue = this.properties.get(property);

        if (Objects.equals(currentValue, value)) {
            return this;
        }

        if (this.propertyValueTable == null) {
            throw new IllegalStateException("Property value table has not been initialized for " + this.properties);
        }

        // Look up the canonical target state.
        IBlockState target = this.propertyValueTable.get(property, value);

        if (target == null) {
            throw invalidValue(property, value);
        }

        // No runtime Unlisted Values to preserve.
        boolean hasUnlistedValues = false;
        for (IProperty<?> key : this.properties.keySet()) {
            if (key instanceof IUnlistedProperty<?>) {
                hasUnlistedValues = true;
                break;
            }
        }

        if (!hasUnlistedValues) {
            return target;
        }

        if (!(target instanceof BlockState targetState)) {
            throw new IllegalStateException(
                "Unexpected state implementation: " + target.getClass()
                    .getName());
        }

        // Start with the target's canonical Listed Values.
        ImmutableMap.Builder<IProperty<?>, Comparable<?>> builder = ImmutableMap.builder();
        builder.putAll(targetState.properties);

        // Preserve the source's runtime Unlisted Values.
        for (Map.Entry<IProperty<?>, Comparable<?>> entry : this.properties.entrySet()) {
            if (entry.getKey() instanceof IUnlistedProperty<?>) {
                builder.put(entry);
            }
        }

        // The derived state must use the target's transition table.
        return new BlockState(this.block, builder.build(), this.unlistedProperties, targetState.propertyValueTable);
    }

    @Override
    public <T extends Comparable<T>> IBlockState withProperty(IUnlistedProperty<T> property, T value) {
        validateProperty(property, value);

        Comparable<?> currentValue = this.properties.get(property);
        if (Objects.equals(currentValue, value)) {
            return this;
        }

        ImmutableMap.Builder<IProperty<?>, Comparable<?>> builder = ImmutableMap.builder();

        boolean replaced = false;
        for (Map.Entry<IProperty<?>, Comparable<?>> entry : this.properties.entrySet()) {
            if (entry.getKey()
                .equals(property)) {
                builder.put(property, value);
                replaced = true;
            } else {
                builder.put(entry);
            }
        }

        if (!replaced) {
            builder.put(property, value);
        }

        return new BlockState(this.block, builder.build(), this.unlistedProperties, this.propertyValueTable);
    }

    private <T extends Comparable<T>> void validateProperty(IProperty<T> property, T value) {
        if (property == null) {
            throw new IllegalArgumentException("Property cannot be null");
        }

        boolean exists;

        if (property instanceof IListedProperty<?>) {
            exists = this.properties.containsKey(property);
        } else if (property instanceof IUnlistedProperty<?>) {
            exists = this.unlistedProperties.contains(property);
        } else {
            exists = false;
        }

        if (!exists) {
            throw new IllegalArgumentException("Property " + property.getName() + " does not exist");
        }

        if (value == null || !property.isValid(value)) {
            throw invalidValue(property, value);
        }
    }

    @Override
    public IBlockState resolveUnlistedProperties(IBlockAccess access, BlockPos pos) {
        ImmutableMap.Builder<IProperty<?>, Comparable<?>> builder = ImmutableMap.builder();

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : this.properties.entrySet()) {
            if (!(entry.getKey() instanceof IUnlistedProperty<?>)) builder.put(entry);
        }

        for (IUnlistedProperty<?> property : this.unlistedProperties) {
            builder.put(property, property.getValue(access, pos));
        }

        return new BlockState(this.block, builder.build(), this.unlistedProperties, this.propertyValueTable);
    }

    private IllegalArgumentException invalidValue(IProperty<?> property, Object value) {
        return new IllegalArgumentException(
            "Cannot set property " + property
                .getName() + " to " + value + " on block " + Helpers.getLocation(this.block));
    }

    @Override
    public boolean setBlockState(World world, BlockPos pos) {
        return this.setBlockState(world, pos, 3);
    }

    @Override
    public boolean setBlockState(World world, BlockPos pos, int flags) {
        if (pos.getBlock(world) != this.block) return false;

        world.setBlock(pos.getX(), pos.getY(), pos.getZ(), this.block, pos.getBlockMetadata(world), flags);

        Chunk chunk = pos.getChunk(world);
        ExtendedBlockStorage ebs = chunk.getBlockStorageArray()[pos.getY() >> 4];
        if (ebs == null) return false;

        BlockStateStorage storage = BlockStateStorage.getStorage(ebs, true);
        if (storage == null) return false;

        int x = pos.getX() & 15;
        int y = pos.getY() & 15;
        int z = pos.getZ() & 15;
        ImmutableMap.Builder<IProperty<?>, Comparable<?>> builder = ImmutableMap.builder();

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : this.properties.entrySet()) {
            if (entry.getKey() instanceof IListedProperty<?>) {
                builder.put(entry);
            }
        }

        ImmutableMap<IProperty<?>, Comparable<?>> listedProperties = builder.build();

        if (listedProperties.isEmpty()) {
            storage.setBlockState(x, y, z, null);
        } else {
            IBlockState listedState = new BlockState(
                this.block,
                listedProperties,
                this.unlistedProperties,
                this.propertyValueTable);

            storage.setBlockState(x, y, z, listedState);
        }

        // Apply Unlisted properties to the world.
        for (Map.Entry<IProperty<?>, Comparable<?>> entry : this.properties.entrySet()) {
            if (!(entry.getKey() instanceof IUnlistedProperty<?>property)) {
                continue;
            }

            applyUnlistedProperty(world, pos, property, entry.getValue());
        }

        chunk.setChunkModified();

        return true;
    }

    private static <T extends Comparable<T>> void applyUnlistedProperty(World world, BlockPos pos,
        IUnlistedProperty<T> property, Comparable<?> value) {
        property.setValue(
            world,
            pos,
            property.getType()
                .cast(value));
    }

    @Override
    public int hashCode() {
        return 31 * System.identityHashCode(this.block) + this.properties.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BlockState other)) {
            return false;
        }

        return this.block == other.block && this.properties.equals(other.properties);
    }

    @Override
    public String toString() {
        return this.block + "[" + this.properties + "]";
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound propertiesTag = new NBTTagCompound();

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : this.properties.entrySet()) {
            if (!(entry.getKey() instanceof IListedProperty<?>property)) continue;
            propertiesTag.setString(property.getName(), getPropertyValueName(property, entry.getValue()));
        }

        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("Properties", propertiesTag);

        return tag;
    }

    private static <T extends Comparable<T>> String getPropertyValueName(IListedProperty<T> property,
        Comparable<?> value) {
        return property.getName(
            property.getType()
                .cast(value));
    }

    @Override
    public IBlockState deserializeNBT(NBTTagCompound tag) {
        if (tag == null) return this;
        NBTTagCompound propertiesTag = tag.getCompoundTag("Properties");
        IBlockState state = this;

        for (IProperty<?> property : this.properties.keySet()) {
            if (!(property instanceof IListedProperty<?>listedProperty)) continue;

            String valueName = propertiesTag.getString(property.getName());
            if (valueName.isEmpty()) continue;

            state = parseListedProperty(state, listedProperty, valueName);
        }

        return state;
    }

    private static <T extends Comparable<T>> IBlockState parseListedProperty(IBlockState state,
        IListedProperty<T> property, String valueName) {
        Optional<T> value = property.parseValue(valueName);

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                "Invalid value '" + valueName + "' for property '" + property.getName() + "'");
        }

        return state.withProperty(property, value.get());
    }

    protected void buildPropertyValueTable(Map<ImmutableMap<IProperty<?>, Comparable<?>>, IBlockState> stateMap) {
        if (this.propertyValueTable != null) {
            throw new IllegalStateException("Property value table already initialized");
        }

        Table<IProperty<?>, Comparable<?>, IBlockState> table = HashBasedTable.create();

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : this.properties.entrySet()) {
            IProperty<?> property = entry.getKey();

            if (!(property instanceof IListedProperty<?>listedProperty)) {
                continue;
            }

            for (Comparable<?> value : listedProperty.getAllowedValues()) {
                if (Objects.equals(value, entry.getValue())) {
                    continue;
                }

                ImmutableMap<IProperty<?>, Comparable<?>> properties = getPropertiesWithValue(property, value);

                IBlockState target = stateMap.get(properties);

                if (target == null) {
                    throw new IllegalStateException("Missing state transition: " + property.getName() + "=" + value);
                }

                table.put(property, value, target);
            }
        }

        this.propertyValueTable = ImmutableTable.copyOf(table);
    }

    private ImmutableMap<IProperty<?>, Comparable<?>> getPropertiesWithValue(IProperty<?> property,
        Comparable<?> value) {
        ImmutableMap.Builder<IProperty<?>, Comparable<?>> builder = ImmutableMap.builder();

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : this.properties.entrySet()) {
            builder.put(
                entry.getKey(),
                entry.getKey()
                    .equals(property) ? value : entry.getValue());
        }

        return builder.build();
    }

    @Override
    public Map<String, String> toMap() {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : this.properties.entrySet()) {
            builder.put(
                entry.getKey()
                    .getName(),
                getPropertyValueString(entry.getKey(), entry.getValue()));
        }

        return builder.build();
    }

    private static <T extends Comparable<T>> String getPropertyValueString(IProperty<T> property, Comparable<?> value) {
        return property.valueToString(
            property.getType()
                .cast(value));
    }
}

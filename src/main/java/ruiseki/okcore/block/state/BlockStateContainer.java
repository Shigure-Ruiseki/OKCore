
package ruiseki.okcore.block.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Table;

import ruiseki.okcore.block.state.property.IListedProperty;
import ruiseki.okcore.block.state.property.IProperty;
import ruiseki.okcore.block.state.property.IUnlistedProperty;
import ruiseki.okcore.event.blockstate.BlockStateContainerEvent;
import ruiseki.okcore.helper.MathHelpers;

public class BlockStateContainer {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");

    private final Block block;

    /**
     * All properties indexed by name.
     * Includes both listed and unlisted properties.
     */
    private final ImmutableSortedMap<String, IProperty<?>> properties;

    private final ImmutableList<IListedProperty<?>> listedProperties;

    private final ImmutableSet<IUnlistedProperty<?>> unlistedProperties;

    private final ImmutableList<IBlockState> validStates;

    /**
     * Maps listed property combinations to their canonical states.
     * Unlisted properties are intentionally excluded.
     */
    private final ImmutableMap<ImmutableMap<IProperty<?>, Comparable<?>>, IBlockState> stateMap;

    public BlockStateContainer(Block block, IProperty<?>... properties) {
        this(block, collectProperties(block, properties));
    }

    private BlockStateContainer(Block block, List<IProperty<?>> properties) {
        if (block == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }

        if (properties == null) {
            throw new IllegalArgumentException("Properties cannot be null");
        }

        this.block = block;

        ImmutableSortedMap.Builder<String, IProperty<?>> propertiesBuilder = ImmutableSortedMap.naturalOrder();

        ImmutableList.Builder<IListedProperty<?>> listedBuilder = ImmutableList.builder();

        ImmutableSet.Builder<IUnlistedProperty<?>> unlistedBuilder = ImmutableSet.builder();

        Set<IProperty<?>> propertySet = new HashSet<>();
        Set<String> propertyNames = new HashSet<>();

        for (IProperty<?> property : properties) {
            if (property == null) {
                throw new IllegalArgumentException("Property cannot be null");
            }

            String name = property.getName();

            if (name == null || !NAME_PATTERN.matcher(name)
                .matches()) {
                throw new IllegalArgumentException("Invalid property name: " + name);
            }

            if (!propertySet.add(property)) {
                throw new IllegalArgumentException("Duplicate property: " + name);
            }

            if (!propertyNames.add(name)) {
                throw new IllegalArgumentException("Duplicate property name: " + name);
            }

            propertiesBuilder.put(name, property);

            if (property instanceof IListedProperty<?>listed) {
                validateListedProperty(listed);
                listedBuilder.add(listed);
            } else if (property instanceof IUnlistedProperty<?>unlisted) {
                unlistedBuilder.add(unlisted);
            } else {
                throw new IllegalArgumentException("Unsupported property type: " + property.getClass());
            }
        }

        this.properties = propertiesBuilder.build();
        this.listedProperties = listedBuilder.build();
        this.unlistedProperties = unlistedBuilder.build();

        Map<ImmutableMap<IProperty<?>, Comparable<?>>, IBlockState> states = new LinkedHashMap<>();

        generateStates(states);

        this.stateMap = ImmutableMap.copyOf(states);
        this.validStates = ImmutableList.copyOf(states.values());

        initializeTransitions();
    }

    private static <T extends Comparable<T>> void validateListedProperty(IListedProperty<T> property) {
        Collection<T> values = property.getAllowedValues();

        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Listed property must have allowed values: " + property.getName());
        }

        Set<T> uniqueValues = new HashSet<>();
        Set<String> uniqueNames = new HashSet<>();

        for (T value : values) {
            if (value == null || !property.isValid(value)) {
                throw new IllegalArgumentException("Invalid allowed value for property: " + property.getName());
            }

            if (!uniqueValues.add(value)) {
                throw new IllegalArgumentException("Duplicate allowed value for property: " + property.getName());
            }

            String valueName = property.getName(value);

            if (valueName == null || !NAME_PATTERN.matcher(valueName)
                .matches()) {
                throw new IllegalArgumentException(
                    "Invalid value name '" + valueName + "' for property: " + property.getName());
            }

            if (!uniqueNames.add(valueName)) {
                throw new IllegalArgumentException(
                    "Duplicate value name '" + valueName + "' for property: " + property.getName());
            }
        }
    }

    private void generateStates(Map<ImmutableMap<IProperty<?>, Comparable<?>>, IBlockState> states) {
        for (List<Comparable<?>> values : MathHelpers.cartesianProduct(getAllowedValues())) {
            ImmutableMap.Builder<IProperty<?>, Comparable<?>> builder = ImmutableMap.builder();

            for (int i = 0; i < this.listedProperties.size(); i++) {
                builder.put(this.listedProperties.get(i), values.get(i));
            }

            ImmutableMap<IProperty<?>, Comparable<?>> stateProperties = builder.build();

            IBlockState state = createState(stateProperties);

            states.put(stateProperties, state);
        }
    }

    /**
     * Factory method for creating canonical states.
     * Can be overridden by specialized containers.
     */
    protected BlockState createState(ImmutableMap<IProperty<?>, Comparable<?>> properties) {
        return new BlockState(this.block, properties, this.unlistedProperties);
    }

    private List<Iterable<? extends Comparable<?>>> getAllowedValues() {
        List<Iterable<? extends Comparable<?>>> values = new ArrayList<>();

        for (IListedProperty<?> property : this.listedProperties) {
            values.add(property.getAllowedValues());
        }

        return values;
    }

    private void initializeTransitions() {
        for (IBlockState state : this.validStates) {
            ((BlockState) state).buildPropertyValueTable(this.stateMap);
        }
    }

    private <T extends Comparable<T>> void addTransitions(IBlockState state, IListedProperty<T> property,
        Table<IProperty<?>, Comparable<?>, IBlockState> transitions) {
        T currentValue = state.getValue(property);

        for (T value : property.getAllowedValues()) {
            if (currentValue.equals(value)) {
                continue;
            }

            Map<IProperty<?>, Comparable<?>> values = new LinkedHashMap<>(state.getProperties());

            values.put(property, value);

            IBlockState target = this.stateMap.get(ImmutableMap.copyOf(values));

            if (target == null) {
                throw new IllegalStateException(
                    "Missing state transition for property " + property.getName() + " with value " + value);
            }

            transitions.put(property, value, target);
        }
    }

    public Block getBlock() {
        return this.block;
    }

    /**
     * Returns all properties indexed by their names.
     */
    public ImmutableSortedMap<String, IProperty<?>> getProperties() {
        return this.properties;
    }

    public IProperty<?> getProperty(String name) {
        return this.properties.get(name);
    }

    public ImmutableList<IListedProperty<?>> getListedProperties() {
        return this.listedProperties;
    }

    public ImmutableSet<IUnlistedProperty<?>> getUnlistedProperties() {
        return this.unlistedProperties;
    }

    public ImmutableList<IBlockState> getValidStates() {
        return this.validStates;
    }

    public IBlockState getBaseState() {
        return this.validStates.get(0);
    }

    public boolean isListedProperty(IProperty<?> property) {
        return this.listedProperties.contains(property);
    }

    public boolean isUnlistedProperty(IProperty<?> property) {
        return this.unlistedProperties.contains(property);
    }

    @Override
    public String toString() {
        return "BlockStateContainer{" + "block="
            + this.block
            + ", properties="
            + this.properties.keySet()
            + ", validStates="
            + this.validStates.size()
            + '}';
    }

    public static class Builder {

        private final Block block;
        private final List<IProperty<?>> properties = new ArrayList<>();

        public Builder(Block block) {
            if (block == null) {
                throw new IllegalArgumentException("Block cannot be null");
            }
            this.block = block;
        }

        public Builder add(IProperty<?> property) {
            if (property == null) {
                throw new IllegalArgumentException("Property cannot be null");
            }

            this.properties.add(property);
            return this;
        }

        public Builder add(IProperty<?>... properties) {
            if (properties == null) {
                throw new IllegalArgumentException("Properties cannot be null");
            }

            for (IProperty<?> property : properties) {
                add(property);
            }

            return this;
        }

        public Builder add(Collection<? extends IProperty<?>> properties) {
            if (properties == null) {
                throw new IllegalArgumentException("Properties cannot be null");
            }

            for (IProperty<?> property : properties) {
                add(property);
            }

            return this;
        }

        public BlockStateContainer build() {
            MinecraftForge.EVENT_BUS.post(new BlockStateContainerEvent.Build(block, this));
            return new BlockStateContainer(this.block, this.properties);
        }
    }

    private static List<IProperty<?>> collectProperties(Block block, IProperty<?>... properties) {
        Builder builder = new Builder(block);
        builder.add(properties);
        MinecraftForge.EVENT_BUS.post(new BlockStateContainerEvent.Build(block, builder));
        return new ArrayList<>(builder.properties);
    }
}

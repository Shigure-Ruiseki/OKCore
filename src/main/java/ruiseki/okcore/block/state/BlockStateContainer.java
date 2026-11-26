
package ruiseki.okcore.block.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;

import ruiseki.okcore.block.state.property.IListedProperty;
import ruiseki.okcore.block.state.property.IProperty;
import ruiseki.okcore.block.state.property.IUnlistedProperty;
import ruiseki.okcore.event.blockstate.BlockStateContainerEvent;
import ruiseki.okcore.helper.MathHelpers;

public class BlockStateContainer {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");

    private final Block block;

    private final ImmutableSortedMap<String, IListedProperty<?>> properties;
    private final ImmutableList<IBlockState> validStates;

    private final ImmutableSet<IUnlistedProperty<?>> unlistedProperties;

    @Nonnull
    protected BlockState createState(@Nonnull Block block,
        @Nonnull ImmutableMap<IListedProperty<?>, Comparable<?>> properties,
        @Nullable ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
        return new BlockState(block, properties, unlistedProperties, null, null);
    }

    public BlockStateContainer(@Nonnull Block block, IListedProperty<?>[] properties,
        IUnlistedProperty<?>[] unlistedProperties) {
        this(block, properties, buildUnlistedMap(unlistedProperties));
    }

    private static ImmutableMap<IUnlistedProperty<?>, Optional<?>> buildUnlistedMap(
        IUnlistedProperty<?>[] unlistedProperties) {
        ImmutableMap.Builder<IUnlistedProperty<?>, Optional<?>> builder = ImmutableMap.builder();
        for (IUnlistedProperty<?> p : unlistedProperties) {
            builder.put(p, Optional.empty());
        }
        return builder.build();
    }

    protected BlockStateContainer(Block blockIn, IListedProperty<?>[] properties,
        ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {

        this.block = blockIn;
        Map<String, IListedProperty<?>> map = Maps.newHashMap();

        for (IListedProperty<?> iproperty : properties) {
            validateProperty(blockIn, iproperty);
            map.put(iproperty.getName(), iproperty);
        }
        this.properties = ImmutableSortedMap.copyOf(map);
        Map<Map<IListedProperty<?>, Comparable<?>>, BlockState> map2 = Maps.newLinkedHashMap();
        List<BlockState> list1 = Lists.newArrayList();
        for (List<Comparable<?>> list : MathHelpers.cartesianProduct(this.getAllowedValues())) {
            Map<IListedProperty<?>, Comparable<?>> map1 = MathHelpers.createMap(this.properties.values(), list);
            BlockState state = createState(blockIn, ImmutableMap.copyOf(map1), unlistedProperties);
            map2.put(map1, state);
            list1.add(state);
        }

        for (BlockState state : list1) {
            state.buildPropertyValueTable(map2);
        }
        this.validStates = ImmutableList.copyOf(list1);

        ImmutableSet.Builder<IUnlistedProperty<?>> builder = ImmutableSet.builder();
        for (IUnlistedProperty<?> property : unlistedProperties.keySet()) {
            builder.add(property);
        }
        this.unlistedProperties = builder.build();
    }

    public static <T extends Comparable<T>> String validateProperty(Block block, IListedProperty<T> property) {
        String s = property.getName();
        if (!NAME_PATTERN.matcher(s)
            .matches()) {
            throw new IllegalArgumentException("Block: " + block.getClass() + " has invalidly named property: " + s);
        } else {
            for (T t : property.getAllowedValues()) {
                String s1 = property.getName(t);

                if (!NAME_PATTERN.matcher(s1)
                    .matches()) {
                    throw new IllegalArgumentException(
                        "Block: " + block.getClass() + " has property: " + s + " with invalidly named value: " + s1);
                }
            }

            return s;
        }
    }

    public ImmutableList<IBlockState> getValidStates() {
        return this.validStates;
    }

    private List<Iterable<Comparable<?>>> getAllowedValues() {
        List<Iterable<Comparable<?>>> list = Lists.newArrayList();
        ImmutableCollection<IListedProperty<?>> immutablecollection = this.properties.values();
        UnmodifiableIterator unmodifiableiterator = immutablecollection.iterator();
        while (unmodifiableiterator.hasNext()) {
            IListedProperty<?> iproperty = (IListedProperty) unmodifiableiterator.next();
            list.add(((IListedProperty) iproperty).getAllowedValues());
        }
        return list;
    }

    public IBlockState getBaseState() {
        return this.validStates.getFirst();
    }

    public Block getBlock() {
        return this.block;
    }

    public Collection<IListedProperty<?>> getProperties() {
        return this.properties.values();
    }

    public Collection<IUnlistedProperty<?>> getUnlistedProperties() {
        return unlistedProperties;
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
        private final List<IListedProperty<?>> properties = new ArrayList<>();
        private final List<IUnlistedProperty<?>> unlistedProperties = new ArrayList<>();

        public Builder(Block block) {
            if (block == null) {
                throw new IllegalArgumentException("Block cannot be null");
            }
            this.block = block;
        }

        public Builder add(IListedProperty<?> property) {
            if (property == null) {
                throw new IllegalArgumentException("Property cannot be null");
            }

            this.properties.add(property);
            return this;
        }

        public Builder add(IListedProperty<?>[] properties) {
            if (properties == null) {
                throw new IllegalArgumentException("Properties cannot be null");
            }

            for (IListedProperty<?> property : properties) {
                add(property);
            }

            return this;
        }

        public Builder add(IUnlistedProperty<?> property) {
            if (property == null) {
                throw new IllegalArgumentException("Property cannot be null");
            }

            this.unlistedProperties.add(property);
            return this;
        }

        public Builder add(IUnlistedProperty<?>[] properties) {
            if (properties == null) {
                throw new IllegalArgumentException("Properties cannot be null");
            }

            for (IUnlistedProperty<?> property : properties) {
                add(property);
            }

            return this;
        }

        public Builder add(IProperty<?>... properties) {
            if (properties == null) {
                throw new IllegalArgumentException("Properties cannot be null");
            }
            for (IProperty<?> property : properties) {
                if (property instanceof IUnlistedProperty<?>unlisted) {
                    add(unlisted);
                }
                if (property instanceof IListedProperty<?>listed) {
                    add(listed);
                }
            }
            return this;
        }

        public BlockStateContainer build() {
            MinecraftForge.EVENT_BUS.post(new BlockStateContainerEvent.Build(block, this));
            return new BlockStateContainer(
                this.block,
                this.properties.toArray(new IListedProperty<?>[0]),
                this.unlistedProperties.toArray(new IUnlistedProperty<?>[0]));
        }
    }
}

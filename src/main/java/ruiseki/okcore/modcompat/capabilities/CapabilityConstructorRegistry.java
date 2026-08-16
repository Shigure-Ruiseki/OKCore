package ruiseki.okcore.modcompat.capabilities;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityCache;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.init.ModBase;

/**
 * Registry for capabilities created by this mod using CapabilityCache.
 *
 * @author rubensworks
 */
public class CapabilityConstructorRegistry {

    private final Map<Class<? extends TileEntity>, List<ICapabilityConstructor<?, ? extends TileEntity, ? extends TileEntity>>> capabilityConstructorsTile = Maps
        .newIdentityHashMap();
    private final Map<Class<? extends Entity>, List<ICapabilityConstructor<?, ? extends Entity, ? extends Entity>>> capabilityConstructorsEntity = Maps
        .newIdentityHashMap();
    private final Map<Class<? extends Item>, List<ICapabilityConstructor<?, ? extends Item, ? extends ItemStack>>> capabilityConstructorsItem = Maps
        .newIdentityHashMap();

    private Collection<Pair<Class<?>, ICapabilityConstructor<?, ?, ?>>> capabilityConstructorsTileSuper = Sets
        .newHashSet();
    private Collection<Pair<Class<?>, ICapabilityConstructor<?, ?, ?>>> capabilityConstructorsEntitySuper = Sets
        .newHashSet();
    private Collection<Pair<Class<?>, ICapabilityConstructor<?, ?, ?>>> capabilityConstructorsItemSuper = Sets
        .newHashSet();

    protected final ModBase mod;
    protected boolean baked = false;
    protected boolean registeredTileEventListener = false;
    protected boolean registeredEntityEventListener = false;
    protected boolean registeredItemStackEventListener = false;

    public CapabilityConstructorRegistry(ModBase mod) {
        this.mod = mod;
    }

    protected ModBase getMod() {
        return mod;
    }

    protected void checkNotBaked() {
        if (baked) {
            throw new IllegalStateException("Please register capabilities before pre-init.");
        }
    }

    public <T extends TileEntity> void registerTile(Class<T> clazz, ICapabilityConstructor<?, T, T> constructor) {
        checkNotBaked();
        capabilityConstructorsTile.computeIfAbsent(clazz, k -> Lists.newArrayList())
            .add(constructor);

        if (!registeredTileEventListener) {
            registeredTileEventListener = true;
            MinecraftForge.EVENT_BUS.register(new TileEventListener());
        }
    }

    public <T extends Entity> void registerEntity(Class<T> clazz, ICapabilityConstructor<?, T, T> constructor) {
        checkNotBaked();
        capabilityConstructorsEntity.computeIfAbsent(clazz, k -> Lists.newArrayList())
            .add(constructor);

        if (!registeredEntityEventListener) {
            registeredEntityEventListener = true;
            MinecraftForge.EVENT_BUS.register(new EntityEventListener());
        }
    }

    public <T extends Item> void registerItem(Class<T> clazz, ICapabilityConstructor<?, T, ItemStack> constructor) {
        checkNotBaked();
        capabilityConstructorsItem.computeIfAbsent(clazz, k -> Lists.newArrayList())
            .add(constructor);

        if (!registeredItemStackEventListener) {
            registeredItemStackEventListener = true;
            MinecraftForge.EVENT_BUS.register(new ItemStackEventListener());
        }
    }

    public <K, V> void registerInheritableTile(Class<K> clazz, ICapabilityConstructor<?, V, V> constructor) {
        checkNotBaked();
        capabilityConstructorsTileSuper.add(Pair.of(clazz, constructor));

        if (!registeredTileEventListener) {
            registeredTileEventListener = true;
            MinecraftForge.EVENT_BUS.register(new TileEventListener());
        }
    }

    public <K, V> void registerInheritableEntity(Class<K> clazz, ICapabilityConstructor<?, V, V> constructor) {
        checkNotBaked();
        capabilityConstructorsEntitySuper.add(Pair.of(clazz, constructor));

        if (!registeredEntityEventListener) {
            registeredEntityEventListener = true;
            MinecraftForge.EVENT_BUS.register(new EntityEventListener());
        }
    }

    public <T> void registerInheritableItem(Class<T> clazz,
        ICapabilityConstructor<?, ?, ? extends ItemStack> constructor) {
        checkNotBaked();
        capabilityConstructorsItemSuper.add(Pair.of(clazz, constructor));

        if (!registeredItemStackEventListener) {
            registeredItemStackEventListener = true;
            MinecraftForge.EVENT_BUS.register(new ItemStackEventListener());
        }
    }

    protected <T> void onLoad(
        Map<Class<? extends T>, List<ICapabilityConstructor<?, ? extends T, ? extends T>>> allConstructors,
        Collection<Pair<Class<?>, ICapabilityConstructor<?, ?, ?>>> allInheritableConstructors, T object,
        AttachCapabilitiesEvent<?> event, Class<? extends T> baseClass) {
        onLoad(allConstructors, allInheritableConstructors, object, object, event, baseClass);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <K, V> void onLoad(
        Map<Class<? extends K>, List<ICapabilityConstructor<?, ? extends K, ? extends V>>> allConstructors,
        Collection<Pair<Class<?>, ICapabilityConstructor<?, ?, ?>>> allInheritableConstructors, K keyObject,
        V valueObject, AttachCapabilitiesEvent<?> event, Class<? extends K> baseClass) {
        boolean initialized = baked || Helpers.isMinecraftInitialized();
        if (!baked && Helpers.isMinecraftInitialized()) {
            bake();
        }

        List<ICapabilityConstructor<?, ? extends K, ? extends V>> matchedConstructors = Lists.newArrayList();

        Collection<ICapabilityConstructor<?, ? extends K, ? extends V>> constructors = allConstructors
            .get(keyObject.getClass());
        if (constructors != null) {
            for (ICapabilityConstructor<?, ? extends K, ? extends V> constructor : constructors) {
                if (initialized || constructor.getCapability() != null) {
                    matchedConstructors.add(constructor);
                }
            }
        }

        for (Pair<Class<?>, ICapabilityConstructor<?, ?, ?>> constructorEntry : allInheritableConstructors) {
            if ((initialized || constructorEntry.getRight()
                .getCapability() != null)
                && (keyObject == baseClass || constructorEntry.getLeft() == keyObject
                    || constructorEntry.getLeft()
                        .isInstance(keyObject))) {
                matchedConstructors.add((ICapabilityConstructor) constructorEntry.getRight());
            }
        }

        if (!matchedConstructors.isEmpty()) {
            CapabilityCache cache = new CapabilityCache();

            for (ICapabilityConstructor<?, ? extends K, ? extends V> constructor : matchedConstructors) {
                Capability<?> cap = constructor.getCapability();
                if (cap != null) {
                    cache.addCapabilityResolver(
                        new ConstructorCapabilityResolver(cap, keyObject, valueObject, constructor));
                }
            }

            // Tạo Provider bọc CapabilityCache
            ResourceLocation providerId = new ResourceLocation(getMod().getModId(), "capability_cache");
            if (!event.getCapabilities()
                .containsKey(providerId)) {
                event.addCapability(providerId, new CapabilityCacheProvider(cache));
            } else {
                getMod().getLoggerHelper()
                    .log(Level.DEBUG, "Duplicate capability cache registration for " + keyObject);
            }
        }
    }

    protected <K, V> void removeNullCapabilities(
        Map<Class<? extends K>, List<ICapabilityConstructor<?, ? extends K, ? extends V>>> allConstructors,
        Collection<Pair<Class<?>, ICapabilityConstructor<?, ?, ?>>> allInheritableConstructors) {
        Multimap<Class<? extends K>, ICapabilityConstructor<?, ? extends K, ? extends V>> toRemoveMap = HashMultimap
            .create();
        for (Class<? extends K> key : allConstructors.keySet()) {
            Collection<ICapabilityConstructor<?, ? extends K, ? extends V>> constructors = allConstructors.get(key);
            for (ICapabilityConstructor<?, ? extends K, ? extends V> constructor : constructors) {
                if (constructor.getCapability() == null) {
                    toRemoveMap.put(key, constructor);
                }
            }
        }
        for (Map.Entry<Class<? extends K>, ICapabilityConstructor<?, ? extends K, ? extends V>> entry : toRemoveMap
            .entries()) {
            List<ICapabilityConstructor<?, ? extends K, ? extends V>> constructors = allConstructors
                .get(entry.getKey());
            constructors.remove(entry.getValue());
        }

        List<Pair<Class<?>, ICapabilityConstructor<?, ?, ?>>> toRemoveInheritableList = Lists.newArrayList();
        for (Pair<Class<?>, ICapabilityConstructor<?, ?, ?>> constructorEntry : allInheritableConstructors) {
            if (constructorEntry.getRight()
                .getCapability() == null) {
                toRemoveInheritableList.add(constructorEntry);
            }
        }
        for (Pair<Class<?>, ICapabilityConstructor<?, ?, ?>> toRemove : toRemoveInheritableList) {
            allInheritableConstructors.remove(toRemove);
        }
    }

    public void bake() {
        baked = true;

        removeNullCapabilities(capabilityConstructorsTile, capabilityConstructorsTileSuper);
        removeNullCapabilities(capabilityConstructorsEntity, capabilityConstructorsEntitySuper);
        removeNullCapabilities(capabilityConstructorsItem, capabilityConstructorsItemSuper);

        capabilityConstructorsTileSuper = ImmutableList.copyOf(capabilityConstructorsTileSuper);
        capabilityConstructorsEntitySuper = ImmutableList.copyOf(capabilityConstructorsEntitySuper);
        capabilityConstructorsItemSuper = ImmutableList.copyOf(capabilityConstructorsItemSuper);
    }

    public class TileEventListener {

        @SubscribeEvent
        public void onTileLoad(AttachCapabilitiesEvent<TileEntity> event) {
            onLoad(
                capabilityConstructorsTile,
                capabilityConstructorsTileSuper,
                event.getObject(),
                event,
                TileEntity.class);
        }
    }

    public class EntityEventListener {

        @SubscribeEvent
        public void onEntityLoad(AttachCapabilitiesEvent<Entity> event) {
            onLoad(
                capabilityConstructorsEntity,
                capabilityConstructorsEntitySuper,
                event.getObject(),
                event,
                Entity.class);
        }
    }

    public class ItemStackEventListener {

        @SubscribeEvent
        public void onItemStackLoad(AttachCapabilitiesEvent<ItemStack> event) {
            if (event.getObject() != null) {
                onLoad(
                    capabilityConstructorsItem,
                    capabilityConstructorsItemSuper,
                    event.getObject()
                        .getItem(),
                    event.getObject(),
                    event,
                    Item.class);
            }
        }
    }

    /**
     * Helper Provider bọc CapabilityCache để tích hợp với hệ thống ICapabilityProvider của OKCore.
     */
    public static class CapabilityCacheProvider implements ICapabilityProvider {

        private final CapabilityCache cache;

        public CapabilityCacheProvider(CapabilityCache cache) {
            this.cache = cache;
        }

        public CapabilityCache getCache() {
            return cache;
        }

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
            @Nullable ForgeDirection side) {
            return cache.getCapability(capability, side);
        }
    }
}

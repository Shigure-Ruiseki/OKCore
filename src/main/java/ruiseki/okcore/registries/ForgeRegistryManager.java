package ruiseki.okcore.registries;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ruiseki.okcore.OKCore;

public class ForgeRegistryManager {

    public static final ForgeRegistryManager INSTANCE = new ForgeRegistryManager();
    BiMap<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> registries = HashBiMap.create();
    private BiMap<Class<? extends IForgeRegistryEntry<?>>, ResourceLocation> superTypes = HashBiMap.create();

    private ForgeRegistryManager() {}

    public <T extends IForgeRegistryEntry<T>> ForgeRegistry<T> createRegistry(ResourceLocation name, Class<T> type,
        @Nullable ResourceLocation defaultKey) {
        Set<Class<?>> parents = Sets.newHashSet();
        Sets.SetView<Class<?>> overlappedTypes = Sets.intersection(parents, superTypes.keySet());
        if (!overlappedTypes.isEmpty()) {
            Class<?> foundType = overlappedTypes.iterator()
                .next();
            OKCore.okLog(
                Level.ERROR,
                "Found existing registry of type {} named {}, you cannot create a new registry ({}) with type {}, as {} has a parent of that type",
                foundType,
                superTypes.get(foundType),
                name,
                type,
                type);
            throw new IllegalArgumentException(
                "Duplicate registry parent type found - you can only have one registry for a particular super type");
        }

        ForgeRegistry<T> registry = new ForgeRegistry<>(type, name, defaultKey);

        registries.put(name, registry);
        superTypes.put(type, name);
        return getRegistry(name);
    }

    private void findSuperTypes(Class<?> type, Set<Class<?>> types) {
        if (type == null || type == Object.class) {
            return;
        }
        types.add(type);
        for (Class<?> interfac : type.getInterfaces()) {
            findSuperTypes(interfac, types);
        }
        findSuperTypes(type.getSuperclass(), types);
    }

    @SuppressWarnings("unchecked")
    public <V extends IForgeRegistryEntry<V>> Class<V> getSuperType(ResourceLocation key) {
        return (Class<V>) superTypes.inverse()
            .get(key);
    }

    @SuppressWarnings("unchecked")
    public <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> getRegistry(ResourceLocation key) {
        return (ForgeRegistry<V>) this.registries.get(key);
    }

    public <V extends IForgeRegistryEntry<V>> IForgeRegistry<V> getRegistry(Class<V> cls) {
        return getRegistry(superTypes.get(cls));
    }

    public <V extends IForgeRegistryEntry<V>> ResourceLocation getName(IForgeRegistry<V> reg) {
        return this.registries.inverse()
            .get(reg);
    }

    public <V extends IForgeRegistryEntry<V>> ForgeRegistry<V> getRegistry(ResourceLocation key,
        ForgeRegistryManager other) {
        if (!this.registries.containsKey(key)) {
            ForgeRegistry<V> ot = other.getRegistry(key);
            if (ot == null) return null;
            this.registries.put(key, ot.copy());
            this.superTypes.put(ot.getRegistrySuperType(), key);
        }
        return getRegistry(key);
    }

    public static void fireCreateRegistryEvents() {
        MinecraftForge.EVENT_BUS.post(new RegistryEvent.NewRegistry());
    }

    public static void fireRegistryEvents() {
        fireRegistryEvents(rl -> true);
    }

    public static void fireRegistryEvents(Predicate<ResourceLocation> filter) {
        List<ResourceLocation> keys = Lists.newArrayList(INSTANCE.registries.keySet());
        Collections.sort(
            keys,
            (o1, o2) -> o1.toString()
                .compareToIgnoreCase(o2.toString()));
        for (ResourceLocation rl : keys) {
            if (!filter.test(rl)) continue;
            MinecraftForge.EVENT_BUS.post(
                INSTANCE.getRegistry(rl)
                    .getRegisterEvent(rl));
        }
    }
}

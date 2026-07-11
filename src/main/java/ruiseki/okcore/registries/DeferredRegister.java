package ruiseki.okcore.registries;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.tag.ResourceKey;

/**
 * A simplified version of DeferredRegister designed for Minecraft 1.7.10.
 * Helps to group and defer Block/Item registration until the PreInit stage,
 * while preserving the original RegistryObject structure containing a ResourceKey.
 *
 * @param <T> Typically Item or Block in 1.7.10
 */
public class DeferredRegister<T> {

    private final ResourceKey<T> registryKey;
    private final String modid;
    private final Map<RegistryObject<T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
    private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries.keySet());
    private boolean hasRegistered = false;

    public static <B> DeferredRegister<B> create(ResourceKey<B> regKey, String modid) {
        return new DeferredRegister<>(regKey, modid);
    }

    private DeferredRegister(ResourceKey<T> registryKey, String modid) {
        this.registryKey = registryKey;
        this.modid = modid;
    }

    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(final String name, final Supplier<? extends I> sup) {
        if (hasRegistered) {
            throw new IllegalStateException(
                "Cannot register new entries to DeferredRegister after registerAll() has been called!");
        }
        Objects.requireNonNull(name);
        Objects.requireNonNull(sup);

        ResourceLocation resLoc = new ResourceLocation(this.modid, name);
        RegistryObject<I> ret = RegistryObject.create(resLoc, this.registryKey);

        if (entries.putIfAbsent((RegistryObject<T>) ret, sup) != null) {
            throw new IllegalArgumentException("Duplicate registration ID within the same type: " + name);
        }

        return ret;
    }

    public void register() {
        if (hasRegistered) return;
        this.hasRegistered = true;
        for (Entry<RegistryObject<T>, Supplier<? extends T>> entry : entries.entrySet()) {
            T value = entry.getValue()
                .get();
            RegistryObject<T> regObj = entry.getKey();

            String name = regObj.getId() != null ? regObj.getId()
                .getResourcePath() : "null";
            if (value instanceof IRegistrable<?>registrable) {
                registrable.register(name);
            } else {
                if (value instanceof Item item) {
                    item.setUnlocalizedName(name);
                    GameRegistry.registerItem(item, name);
                } else if (value instanceof Block block) {
                    block.setBlockName(name);
                    GameRegistry.registerBlock(block, null, name);
                }
            }

            regObj.updateReference(value);
        }
    }

    public Collection<RegistryObject<T>> getEntries() {
        return entriesView;
    }

    public String getModid() {
        return this.modid;
    }
}

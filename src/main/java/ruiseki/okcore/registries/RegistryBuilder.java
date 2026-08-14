package ruiseki.okcore.registries;

import net.minecraft.util.ResourceLocation;

public class RegistryBuilder<T extends IForgeRegistryEntry<T>> {

    private ResourceLocation registryName;
    private Class<T> registryType;
    private ResourceLocation optionalDefaultKey;

    public RegistryBuilder<T> setName(ResourceLocation name) {
        this.registryName = name;
        return this;
    }

    public RegistryBuilder<T> setType(Class<T> type) {
        this.registryType = type;
        return this;
    }

    public RegistryBuilder<T> setDefaultKey(ResourceLocation key) {
        this.optionalDefaultKey = key;
        return this;
    }

    public IForgeRegistry<T> create() {
        return ForgeRegistryManager.INSTANCE.createRegistry(registryName, registryType, optionalDefaultKey);
    }
}

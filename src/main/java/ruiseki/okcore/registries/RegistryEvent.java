package ruiseki.okcore.registries;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.okcore.event.generic.GenericEvent;

public class RegistryEvent<T extends IForgeRegistryEntry<T>> extends GenericEvent<T> {

    RegistryEvent(Class<T> clazz) {
        super(clazz);
    }

    /**
     * Register new registries when you receive this event
     */
    public static class NewRegistry extends Event {
    }

    /**
     * Register your objects for the appropriate registry type when you receive this event.
     *
     * <code>event.getRegistry().register(...)</code>
     *
     * The registries will be visited in alphabetic order of their name, except blocks and items,
     * which will be visited FIRST and SECOND respectively.
     *
     * ObjectHolders will reload between Blocks and Items, and after all registries have been visited.
     * 
     * @param <T> The registry top level type
     */
    public static class Register<T extends IForgeRegistryEntry<T>> extends RegistryEvent<T> {

        private final IForgeRegistry<T> registry;
        private final ResourceLocation name;

        public Register(ResourceLocation name, IForgeRegistry<T> registry) {
            super(registry.getRegistrySuperType());
            this.name = name;
            this.registry = registry;
        }

        public IForgeRegistry<T> getRegistry() {
            return registry;
        }

        public ResourceLocation getName() {
            return name;
        }

    }
}

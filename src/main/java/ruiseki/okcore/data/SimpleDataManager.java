package ruiseki.okcore.data;

import java.util.*;
import java.util.function.Predicate;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.datastructure.Resource;

public class SimpleDataManager implements DataManager {

    private final Set<String> namespaces = new HashSet<>();
    private final Map<ResourceLocation, Resource> resourceMap = new HashMap<>();

    public void registerResource(String namespace, ResourceLocation loc, Resource res) {
        this.namespaces.add(namespace);
        this.resourceMap.put(loc, res);
    }

    @Override
    public Set<String> getNamespaces() {
        return Collections.unmodifiableSet(this.namespaces);
    }

    @Override
    public Map<ResourceLocation, Resource> listResources(String type, Predicate<ResourceLocation> filter) {
        Map<ResourceLocation, Resource> filteredMap = new HashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : resourceMap.entrySet()) {
            ResourceLocation loc = entry.getKey();
            String path = loc.getResourcePath();
            if (path.startsWith(type + "/") && filter.test(loc)) {
                filteredMap.put(loc, entry.getValue());
            }
        }
        return filteredMap;
    }

}

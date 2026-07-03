package ruiseki.okcore.data;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.datastructure.Resource;

public class MultiDataManager implements DataManager {

    private final List<DataManager> managers = new ArrayList<>();

    public void addManager(DataManager manager) {
        if (manager != null) {
            this.managers.add(manager);
        }
    }

    @Override
    public Set<String> getNamespaces() {
        Set<String> allNamespaces = new HashSet<>();
        for (DataManager manager : managers) {
            allNamespaces.addAll(manager.getNamespaces());
        }
        return Collections.unmodifiableSet(allNamespaces);
    }

    @Override
    public Map<ResourceLocation, Resource> listResources(String type, Predicate<ResourceLocation> filter) {
        return new CompositeResourceMap(this.managers, type, filter);
    }

    private static class CompositeResourceMap extends AbstractMap<ResourceLocation, Resource> {

        private final List<DataManager> childManagers;
        private final String type;
        private final Predicate<ResourceLocation> filter;

        public CompositeResourceMap(List<DataManager> childManagers, String type, Predicate<ResourceLocation> filter) {
            this.childManagers = childManagers;
            this.type = type;
            this.filter = filter;
        }

        @Override
        public @NotNull Set<Entry<ResourceLocation, Resource>> entrySet() {
            Set<Entry<ResourceLocation, Resource>> allEntries = new LinkedHashSet<>();

            for (DataManager manager : childManagers) {
                Map<ResourceLocation, Resource> childMap = manager.listResources(type, filter);
                allEntries.addAll(childMap.entrySet());
            }

            return allEntries;
        }
    }
}

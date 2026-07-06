package ruiseki.okcore.data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.datastructure.Resource;

public class DatapackManager implements DataManager {

    public static final DatapackManager INSTANCE = new DatapackManager();
    private static final String CONFIG_FILE_NAME = "okcore_datapacks.txt";

    public static class Datapack {

        private final String name;
        private final DataManager dataManager;
        private final boolean isModJar;
        private boolean enabled;

        public Datapack(String name, DataManager dataManager, boolean isModJar) {
            this.name = name;
            this.dataManager = dataManager;
            this.isModJar = isModJar;
            this.enabled = true;
        }

        public String getName() {
            return name;
        }

        public DataManager getDataManager() {
            return dataManager;
        }

        public boolean isModJar() {
            return isModJar;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    private final Map<String, Datapack> datapackRegistry = new ConcurrentHashMap<>();
    private final List<Datapack> orderList = Collections.synchronizedList(new ArrayList<>());
    private final Set<String> persistentDisabledPacks = ConcurrentHashMap.newKeySet();

    private DatapackManager() {}

    public void loadDisabledPacksConfig(File worldDir) {
        persistentDisabledPacks.clear();
        File configFile = new File(worldDir, CONFIG_FILE_NAME);
        if (!configFile.exists()) return;

        try {
            List<String> lines = Files.readAllLines(configFile.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                String trimmed = line.trim()
                    .toLowerCase();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                    persistentDisabledPacks.add(trimmed);
                }
            }
            OKCore.okLog(
                Level.INFO,
                "DatapackManager: Loaded {} disabled datapacks from config.",
                persistentDisabledPacks.size());
        } catch (IOException e) {
            OKCore.okLog(Level.ERROR, "DatapackManager: Failed to read disabled datapacks config!", e);
        }
    }

    public void saveDisabledPacksConfig(File worldDir) {
        if (worldDir == null) return;
        File configFile = new File(worldDir, CONFIG_FILE_NAME);

        List<String> disabledList = new ArrayList<>();

        synchronized (orderList) {
            for (Datapack pack : orderList) {
                if (!pack.isEnabled()) {
                    disabledList.add(
                        pack.getName()
                            .toLowerCase());
                }
            }
        }

        persistentDisabledPacks.clear();
        persistentDisabledPacks.addAll(disabledList);

        try {
            List<String> lines = new ArrayList<>(disabledList.size() + 1);
            lines.add("# Stores the list of disabled datapacks for OKCore. Do not modify mod jars.");
            lines.addAll(disabledList);
            Files.write(configFile.toPath(), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            OKCore.okLog(Level.ERROR, "DatapackManager: Failed to save disabled datapacks config!", e);
        }
    }

    public void registerDatapack(String name, DataManager manager, boolean isModJar) {
        Datapack pack = new Datapack(name, manager, isModJar);
        String lowerName = name.toLowerCase();

        if (!isModJar && persistentDisabledPacks.contains(lowerName)) {
            pack.setEnabled(false);
        }

        datapackRegistry.put(lowerName, pack);
        orderList.add(pack);
    }

    public void clear() {
        datapackRegistry.clear();
        orderList.clear();
    }

    public boolean enableDatapack(String name) {
        Datapack pack = datapackRegistry.get(name.toLowerCase());
        if (pack != null && !pack.isEnabled()) {
            pack.setEnabled(true);
            return true;
        }
        return false;
    }

    public boolean disableDatapack(String name) {
        Datapack pack = datapackRegistry.get(name.toLowerCase());
        if (pack != null && pack.isEnabled() && !pack.isModJar()) {
            pack.setEnabled(false);
            return true;
        }
        return false;
    }

    public Map<String, Datapack> getDatapackRegistry() {
        return Collections.unmodifiableMap(datapackRegistry);
    }

    @Override
    public Set<String> getNamespaces() {
        Set<String> allNamespaces = new HashSet<>();
        synchronized (orderList) {
            for (Datapack pack : orderList) {
                if (pack.isEnabled()) {
                    allNamespaces.addAll(
                        pack.getDataManager()
                            .getNamespaces());
                }
            }
        }
        return Collections.unmodifiableSet(allNamespaces);
    }

    @Override
    public Map<ResourceLocation, Resource> listResources(String type, Predicate<ResourceLocation> filter) {
        return new CompositeResourceMap(orderList, type, filter);
    }

    private static class CompositeResourceMap extends AbstractMap<ResourceLocation, Resource> {

        private final List<Datapack> packs;
        private final String type;
        private final Predicate<ResourceLocation> filter;

        public CompositeResourceMap(List<Datapack> packs, String type, Predicate<ResourceLocation> filter) {
            this.packs = packs;
            this.type = type;
            this.filter = filter;
        }

        @Override
        public @NotNull Set<Entry<ResourceLocation, Resource>> entrySet() {
            Map<ResourceLocation, Resource> mergedMap = new LinkedHashMap<>();
            synchronized (packs) {
                for (Datapack pack : packs) {
                    if (pack.isEnabled()) {
                        mergedMap.putAll(
                            pack.getDataManager()
                                .listResources(type, filter));
                    }
                }
            }
            return mergedMap.entrySet();
        }
    }
}

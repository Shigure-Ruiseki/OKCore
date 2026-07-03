package ruiseki.okcore.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.DataManager;
import ruiseki.okcore.data.MultiJsonResourceReloadListener;
import ruiseki.okcore.tag.entry.TagEntry;
import ruiseki.okcore.tag.entry.TagEntryRegistry;

public class TagManager extends MultiJsonResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    private static TagManager instance;

    private Map<TagKey<?>, Set<TagEntry<?>>> tagToEntriesMap = ImmutableMap.of();
    private Map<TagEntry<?>, Set<TagKey<?>>> entryToTagsCache = ImmutableMap.of();

    public TagManager() {
        super(GSON, "tags");
    }

    public static TagManager getManager() {
        if (instance == null) {
            instance = new TagManager();
        }
        return instance;
    }

    @Override
    protected void apply(Map<ResourceLocation, List<JsonElement>> data, DataManager manager) {
        Map<TagKey<?>, Set<TagEntry<?>>> finalTagsMap = new HashMap<>();

        for (Map.Entry<ResourceLocation, List<JsonElement>> entry : data.entrySet()) {
            ResourceLocation fileId = entry.getKey();
            List<JsonElement> jsonFiles = entry.getValue();

            String fullPath = fileId.getResourcePath();
            int firstSlash = fullPath.indexOf('/');
            if (firstSlash == -1) continue;

            String subfolder = fullPath.substring(0, firstSlash);
            String tagPath = fullPath.substring(firstSlash + 1);

            TagEntry<?> factory = TagEntryRegistry.getFactory(subfolder);
            if (factory == null) {
                OKCore.okLog(
                    Level.WARN,
                    "No TagEntry factory registered for subfolder [{}] from file {}",
                    subfolder,
                    fileId);
                continue;
            }

            ResourceKey<?> registryKey = ResourceKey.createRegistryKey(new ResourceLocation("minecraft", subfolder));
            ResourceLocation tagId = new ResourceLocation(fileId.getResourceDomain(), tagPath);
            TagKey<?> tagKey = TagKey.create(registryKey, tagId);

            Set<TagEntry<?>> accumulatedEntries = finalTagsMap.computeIfAbsent(tagKey, k -> new HashSet<>());

            int fileIndex = 0;
            for (JsonElement root : jsonFiles) {
                fileIndex++;
                try {
                    if (!root.isJsonObject()) continue;
                    JsonObject jsonObject = root.getAsJsonObject();

                    boolean replace = jsonObject.has("replace") && jsonObject.get("replace")
                        .getAsBoolean();

                    if (replace) {
                        int beforeClearCount = accumulatedEntries.size();
                        accumulatedEntries.clear();
                    }

                    List<TagEntry<?>> currentFileEntries = new ArrayList<>();

                    if (jsonObject.has("values") && jsonObject.get("values")
                        .isJsonArray()) {
                        JsonArray valuesArray = jsonObject.getAsJsonArray("values");

                        for (JsonElement element : valuesArray) {
                            if (!element.isJsonPrimitive()) continue;
                            String rawValue = element.getAsString();

                            String[] parts = rawValue.split(":");
                            if (parts.length < 2) continue;

                            String namespace = parts[0];
                            String path = parts[1];
                            int meta = 0;

                            if (parts.length >= 3) {
                                String rawMeta = parts[2];
                                if (!rawMeta.equalsIgnoreCase("#wildcard")) {
                                    try {
                                        meta = Integer.parseInt(rawMeta);
                                    } catch (NumberFormatException ignored) {}
                                }
                            }

                            ResourceLocation entryId = new ResourceLocation(namespace, path);
                            TagEntry<?> newEntry = factory.create(entryId, meta);
                            if (newEntry != null) {
                                currentFileEntries.add(newEntry);
                            }
                        }
                    }

                    accumulatedEntries.addAll(currentFileEntries);
                } catch (IllegalArgumentException | JsonParseException e) {
                    OKCore
                        .okLog(Level.ERROR, "Parsing error loading datapack tag file [{}]: {}", fileId, e.getMessage());
                }
            }
        }

        finalTagsMap.values()
            .removeIf(Set::isEmpty);

        ImmutableMap.Builder<TagKey<?>, Set<TagEntry<?>>> builder = ImmutableMap.builder();
        finalTagsMap.forEach((tagKey, entries) -> builder.put(tagKey, Collections.unmodifiableSet(entries)));

        this.tagToEntriesMap = builder.build();
        this.bakeCache();

        OKCore.okLog(
            Level.INFO,
            "Loaded {} tags natively. TagRegistry has been completely phased out.",
            this.tagToEntriesMap.size());
    }

    @SuppressWarnings("unchecked")
    public <T> Set<TagKey<T>> getTags(TagEntry<T> target) {
        if (target == null || this.entryToTagsCache.isEmpty()) return Collections.emptySet();
        Set<TagKey<T>> cached = (Set<TagKey<T>>) (Set<?>) this.entryToTagsCache.get(target);
        return cached != null ? cached : Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public <T> Set<TagEntry<T>> getEntries(TagKey<T> tagKey) {
        if (tagKey == null) return Collections.emptySet();
        Set<TagEntry<T>> elements = (Set<TagEntry<T>>) (Set<?>) this.tagToEntriesMap.get(tagKey);
        return elements != null ? elements : Collections.emptySet();
    }

    public Map<TagKey<?>, Set<TagEntry<?>>> getTags() {
        return this.tagToEntriesMap;
    }

    @SideOnly(Side.CLIENT)
    public void replaceTags(Map<TagKey<?>, Set<TagEntry<?>>> serverTags) {
        if (serverTags == null) {
            this.tagToEntriesMap = ImmutableMap.of();
            this.entryToTagsCache = ImmutableMap.of();
            return;
        }

        Map<TagKey<?>, Set<TagEntry<?>>> map = Maps.newHashMap();
        serverTags.forEach((tagKey, wrappers) -> {
            if (tagKey != null && wrappers != null) {
                Set<TagEntry<?>> typeSet = map.computeIfAbsent(tagKey, k -> Sets.newHashSet());
                typeSet.addAll(wrappers);
            }
        });

        ImmutableMap.Builder<TagKey<?>, Set<TagEntry<?>>> builder = ImmutableMap.builder();
        map.forEach((tagKey, entries) -> builder.put(tagKey, Collections.unmodifiableSet(entries)));
        this.tagToEntriesMap = builder.build();
        this.bakeCache();
    }

    private void bakeCache() {
        Map<TagEntry<?>, Set<TagKey<?>>> tempCache = new HashMap<>();

        for (Map.Entry<TagKey<?>, Set<TagEntry<?>>> entry : this.tagToEntriesMap.entrySet()) {
            TagKey<?> key = entry.getKey();
            if (key == null || entry.getValue() == null) continue;

            for (TagEntry<?> element : entry.getValue()) {
                if (element != null) {
                    tempCache.computeIfAbsent(element, k -> new HashSet<>())
                        .add(key);
                }
            }
        }

        ImmutableMap.Builder<TagEntry<?>, Set<TagKey<?>>> cacheBuilder = ImmutableMap.builder();
        tempCache.forEach((entry, keys) -> cacheBuilder.put(entry, Collections.unmodifiableSet(keys)));
        this.entryToTagsCache = cacheBuilder.build();
    }
}

package ruiseki.okcore.tag;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.DataManager;
import ruiseki.okcore.data.MultiJsonResourceReloadListener;
import ruiseki.okcore.init.IRegistry;

public class TagManager extends MultiJsonResourceReloadListener implements IRegistry {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting()
        .disableHtmlEscaping()
        .create();
    private static final TagManager INSTANCE = new TagManager();

    private Map<TagKey<?>, Set<TagEntry>> tagToEntriesMap = Collections.emptyMap();
    private Map<ResourceKey<?>, Map<ResourceLocation, Int2ObjectMap<Set<TagKey<?>>>>> registryToTagsCache = Collections
        .emptyMap();

    public TagManager() {
        super(GSON, "tags");
    }

    public static TagManager getManager() {
        return INSTANCE;
    }

    @Override
    protected void apply(Map<ResourceLocation, List<JsonElement>> data, DataManager manager) {
        Map<TagKey<?>, Set<String>> rawTagsMap = new Object2ObjectOpenHashMap<>();

        for (Map.Entry<ResourceLocation, List<JsonElement>> entry : data.entrySet()) {
            ResourceLocation fileId = entry.getKey();
            List<JsonElement> jsonFiles = entry.getValue();

            String fullPath = fileId.getResourcePath();
            int firstSlash = fullPath.indexOf('/');
            if (firstSlash == -1) continue;

            String subfolder = fullPath.substring(0, firstSlash);
            String tagPath = fullPath.substring(firstSlash + 1);

            ResourceKey<?> registryKey = ResourceKey.createRegistryKey(new ResourceLocation(subfolder));
            TagKey<?> tagKey = TagKey.create(registryKey, new ResourceLocation(fileId.getResourceDomain(), tagPath));

            Set<String> accumulatedRawValues = rawTagsMap.computeIfAbsent(tagKey, k -> new ObjectOpenHashSet<>());

            for (JsonElement root : jsonFiles) {
                try {
                    if (!root.isJsonObject()) continue;
                    JsonObject jsonObject = root.getAsJsonObject();

                    boolean replace = jsonObject.has("replace") && jsonObject.get("replace")
                        .getAsBoolean();
                    if (replace) {
                        accumulatedRawValues.clear();
                    }

                    if (jsonObject.has("values") && jsonObject.get("values")
                        .isJsonArray()) {
                        JsonArray valuesArray = jsonObject.getAsJsonArray("values");
                        for (JsonElement element : valuesArray) {
                            if (element.isJsonPrimitive()) {
                                accumulatedRawValues.add(element.getAsString());
                            }
                        }
                    }
                } catch (IllegalArgumentException | JsonParseException e) {
                    OKCore
                        .okLog(Level.ERROR, "Parsing error loading datapack tag file [{}]: {}", fileId, e.getMessage());
                }
            }
        }

        Map<TagKey<?>, Set<TagEntry>> finalTagsMap = new Object2ObjectOpenHashMap<>();

        for (TagKey<?> tagKey : rawTagsMap.keySet()) {
            Set<TagEntry> resolvedEntries = new ObjectOpenHashSet<>();
            resolveTagValues(tagKey, rawTagsMap, resolvedEntries, new ObjectOpenHashSet<>());
            if (!resolvedEntries.isEmpty()) {
                finalTagsMap.put(tagKey, resolvedEntries);
            }
        }

        Map<TagKey<?>, Set<TagEntry>> builder = new Object2ObjectOpenHashMap<>();
        finalTagsMap.forEach((tagKey, entries) -> builder.put(tagKey, Collections.unmodifiableSet(entries)));

        this.tagToEntriesMap = Collections.unmodifiableMap(builder);
        this.bakeCache();

        OKCore.okLog(
            Level.INFO,
            "Loaded {} tags natively. TagRegistry has been completely phased out.",
            this.tagToEntriesMap.size());
    }

    private void resolveTagValues(TagKey<?> currentTag, Map<TagKey<?>, Set<String>> rawTagsMap,
        Set<TagEntry> outEntries, Set<TagKey<?>> visitedTags) {
        if (!visitedTags.add(currentTag)) return;

        Set<String> rawValues = rawTagsMap.get(currentTag);
        if (rawValues == null) return;

        for (String rawValue : rawValues) {
            if (rawValue.startsWith("#")) {
                String tagIdentifier = rawValue.substring(1);
                String[] parts = tagIdentifier.split(":");
                if (parts.length < 2) continue;

                TagKey<?> childTagKey = TagKey.create(currentTag.registry(), new ResourceLocation(parts[0], parts[1]));
                resolveTagValues(childTagKey, rawTagsMap, outEntries, visitedTags);
            } else {
                String[] parts = rawValue.split(":");
                if (parts.length < 2) continue;

                String namespace = parts[0];
                String path = parts[1];
                int meta = 0;

                if (parts.length >= 3) {
                    String rawMeta = parts[2];
                    if (rawMeta.equalsIgnoreCase("#wildcard")) {
                        meta = TagEntry.WILDCARD;
                    } else {
                        try {
                            meta = Integer.parseInt(rawMeta);
                        } catch (NumberFormatException ignored) {
                            meta = TagEntry.WILDCARD;
                        }
                    }
                }

                outEntries.add(new TagEntry(new ResourceLocation(namespace, path), meta));
            }
        }
        visitedTags.remove(currentTag);
    }

    public Set<TagEntry> getEntries(TagKey<?> tagKey) {
        if (tagKey == null) return Collections.emptySet();
        Set<TagEntry> elements = this.tagToEntriesMap.get(tagKey);
        return elements != null ? elements : Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public <T> Set<TagKey<T>> getTags(ResourceKey<T> registryKey, ResourceLocation id, int meta) {
        if (id == null || this.registryToTagsCache.isEmpty()) return Collections.emptySet();

        Map<ResourceLocation, Int2ObjectMap<Set<TagKey<?>>>> idMap = this.registryToTagsCache.get(registryKey);
        if (idMap == null) return Collections.emptySet();

        Int2ObjectMap<Set<TagKey<?>>> metaMap = idMap.get(id);
        if (metaMap == null) return Collections.emptySet();

        Set<TagKey<T>> result = new ObjectOpenHashSet<>();

        Set<TagKey<?>> exactTags = metaMap.get(meta);
        if (exactTags != null) {
            exactTags.forEach(tag -> result.add((TagKey<T>) tag));
        }

        Set<TagKey<?>> wildcardTags = metaMap.get(TagEntry.WILDCARD);
        if (wildcardTags != null) {
            wildcardTags.forEach(tag -> result.add((TagKey<T>) tag));
        }

        return result.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(result);
    }

    public boolean hasTag(ResourceKey<?> registryKey, ResourceLocation id, int meta, TagKey<?> tagKey) {
        if (id == null || tagKey == null || this.registryToTagsCache.isEmpty()) return false;

        Map<ResourceLocation, Int2ObjectMap<Set<TagKey<?>>>> idMap = this.registryToTagsCache.get(registryKey);
        if (idMap == null) return false;

        Int2ObjectMap<Set<TagKey<?>>> metaMap = idMap.get(id);
        if (metaMap == null) return false;

        Set<TagKey<?>> tags = metaMap.get(meta);
        if (tags != null && tags.contains(tagKey)) return true;

        Set<TagKey<?>> wildcardTags = metaMap.get(TagEntry.WILDCARD);
        return wildcardTags != null && wildcardTags.contains(tagKey);
    }

    public Map<TagKey<?>, Set<TagEntry>> getTags() {
        return this.tagToEntriesMap;
    }

    @SideOnly(Side.CLIENT)
    public void replaceTags(Map<TagKey<?>, Set<TagEntry>> serverTags) {
        Map<TagKey<?>, Set<TagEntry>> map = new Object2ObjectOpenHashMap<>();
        Map<TagKey<?>, Set<TagEntry>> builder = new Object2ObjectOpenHashMap<>();
        serverTags.forEach((tagKey, wrappers) -> {
            if (tagKey != null && wrappers != null) {
                Set<TagEntry> typeSet = map.computeIfAbsent(tagKey, k -> new ObjectOpenHashSet<>());
                typeSet.addAll(wrappers);
                builder.put(tagKey, Collections.unmodifiableSet(typeSet));
            }
        });
        this.tagToEntriesMap = Collections.unmodifiableMap(builder);
        this.bakeCache();
    }

    private void bakeCache() {
        Map<ResourceKey<?>, Map<ResourceLocation, Int2ObjectMap<Set<TagKey<?>>>>> tempRegistryMap = new Reference2ObjectOpenHashMap<>();

        for (Map.Entry<TagKey<?>, Set<TagEntry>> entry : this.tagToEntriesMap.entrySet()) {
            TagKey<?> tagKey = entry.getKey();
            if (tagKey == null || entry.getValue() == null) continue;

            ResourceKey<?> registryKey = tagKey.registry();
            Map<ResourceLocation, Int2ObjectMap<Set<TagKey<?>>>> idMap = tempRegistryMap
                .computeIfAbsent(registryKey, k -> new Object2ObjectOpenHashMap<>());

            for (TagEntry value : entry.getValue()) {
                if (value != null) {
                    Int2ObjectMap<Set<TagKey<?>>> metaMap = idMap
                        .computeIfAbsent(value.id(), k -> new Int2ObjectOpenHashMap<>());

                    metaMap.computeIfAbsent(value.meta(), k -> new ObjectOpenHashSet<>())
                        .add(tagKey);
                }
            }
        }

        Map<ResourceKey<?>, Map<ResourceLocation, Int2ObjectMap<Set<TagKey<?>>>>> registryBuilder = new Reference2ObjectOpenHashMap<>();

        tempRegistryMap.forEach((regKey, idMap) -> {
            Map<ResourceLocation, Int2ObjectMap<Set<TagKey<?>>>> immutableIdMap = new Object2ObjectOpenHashMap<>();
            idMap.forEach((id, metaMap) -> {
                Int2ObjectMap<Set<TagKey<?>>> immutableMetaMap = new Int2ObjectOpenHashMap<>();
                metaMap.forEach((meta, tags) -> immutableMetaMap.put(meta, Collections.unmodifiableSet(tags)));
                immutableIdMap.put(id, Int2ObjectMaps.unmodifiable(immutableMetaMap));
            });
            registryBuilder.put(regKey, Collections.unmodifiableMap(immutableIdMap));
        });

        this.registryToTagsCache = Collections.unmodifiableMap(registryBuilder);
    }
}

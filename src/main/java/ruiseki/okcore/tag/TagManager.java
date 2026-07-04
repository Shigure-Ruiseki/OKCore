package ruiseki.okcore.tag;

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
import ruiseki.okcore.tag.entry.ITagEntrySerializer;
import ruiseki.okcore.tag.entry.TagEntry;
import ruiseki.okcore.tag.entry.TagEntryRegistry;

public class TagManager extends MultiJsonResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    private final static TagManager INSTANCE = new TagManager();

    private Map<TagKey<?>, Set<TagEntry<?>>> tagToEntriesMap = ImmutableMap.of();
    private Map<TagEntry<?>, Set<TagKey<?>>> entryToTagsCache = ImmutableMap.of();

    public TagManager() {
        super(GSON, "tags");
    }

    public static TagManager getManager() {
        return INSTANCE;
    }

    @Override
    protected void apply(Map<ResourceLocation, List<JsonElement>> data, DataManager manager) {
        Map<TagKey<?>, Set<String>> rawTagsMap = new HashMap<>();

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

            Set<String> accumulatedRawValues = rawTagsMap.computeIfAbsent(tagKey, k -> new HashSet<>());

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

        Map<TagKey<?>, Set<TagEntry<?>>> finalTagsMap = new HashMap<>();

        for (TagKey<?> tagKey : rawTagsMap.keySet()) {
            Set<TagEntry<?>> resolvedEntries = new HashSet<>();

            String subfolder = tagKey.registry()
                .location()
                .getResourcePath();
            ITagEntrySerializer<?, ?> serializer = TagEntryRegistry.getSerializer(subfolder);

            if (serializer != null) {
                resolveTagValues(tagKey, rawTagsMap, resolvedEntries, serializer, new HashSet<>());
                if (!resolvedEntries.isEmpty()) {
                    finalTagsMap.put(tagKey, resolvedEntries);
                }
            } else {
                OKCore.okLog(
                    Level.WARN,
                    "No TagEntry serializer registered for subfolder [{}] of tag {}",
                    subfolder,
                    tagKey.location());
            }
        }

        ImmutableMap.Builder<TagKey<?>, Set<TagEntry<?>>> builder = ImmutableMap.builder();
        finalTagsMap.forEach((tagKey, entries) -> builder.put(tagKey, Collections.unmodifiableSet(entries)));

        this.tagToEntriesMap = builder.build();
        this.bakeCache();

        OKCore.okLog(
            Level.INFO,
            "Loaded {} tags natively. TagRegistry has been completely phased out.",
            this.tagToEntriesMap.size());
    }

    private void resolveTagValues(TagKey<?> currentTag, Map<TagKey<?>, Set<String>> rawTagsMap,
        Set<TagEntry<?>> outEntries, ITagEntrySerializer<?, ?> serializer, Set<TagKey<?>> visitedTags) {
        if (!visitedTags.add(currentTag)) return;

        Set<String> rawValues = rawTagsMap.get(currentTag);
        if (rawValues == null) return;

        for (String rawValue : rawValues) {
            if (rawValue.startsWith("#")) {
                String tagIdentifier = rawValue.substring(1);
                String[] parts = tagIdentifier.split(":");
                if (parts.length < 2) continue;

                String namespace = parts[0];
                String path = parts[1];

                TagKey<?> childTagKey = TagKey.create(currentTag.registry(), new ResourceLocation(namespace, path));

                resolveTagValues(childTagKey, rawTagsMap, outEntries, serializer, visitedTags);
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
                        } catch (NumberFormatException ignored) {}
                    }
                }

                ResourceLocation entryId = new ResourceLocation(namespace, path);
                TagEntry<?> parsedEntry = serializer.read(entryId, meta);
                if (parsedEntry != null) {
                    outEntries.add(parsedEntry);
                }
            }
        }

        visitedTags.remove(currentTag);
    }

    @SuppressWarnings("unchecked")
    public <T> Set<TagKey<T>> getTags(TagEntry<T> target) {
        if (target == null || this.entryToTagsCache.isEmpty()) return Collections.emptySet();
        Set<TagKey<T>> cached = (Set<TagKey<T>>) (Set<?>) this.entryToTagsCache.get(target);
        return cached != null ? cached : Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public <T> Set<TagKey<T>> getTags(Class<T> type, ResourceLocation id, int meta) {
        if (id == null || this.entryToTagsCache.isEmpty()) return Collections.emptySet();
        Set<TagKey<T>> result = new HashSet<>();
        for (Map.Entry<TagEntry<?>, Set<TagKey<?>>> cacheEntry : this.entryToTagsCache.entrySet()) {
            TagEntry<?> entry = cacheEntry.getKey();
            if (entry.getType() == type) {
                if (entry.getId()
                    .equals(id)) {
                    if (entry.getMeta() == TagEntry.WILDCARD || meta == TagEntry.WILDCARD || entry.getMeta() == meta) {
                        result.addAll((Set<TagKey<T>>) (Set<?>) cacheEntry.getValue());
                    }
                }
            }
        }
        return result.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(result);
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

    public <T> boolean hasTag(Class<T> type, ResourceLocation id, int meta, TagKey<T> tagKey) {
        if (id == null || tagKey == null || this.entryToTagsCache.isEmpty()) return false;

        for (Map.Entry<TagEntry<?>, Set<TagKey<?>>> cacheEntry : this.entryToTagsCache.entrySet()) {
            TagEntry<?> entry = cacheEntry.getKey();

            if (entry.getType() == type && entry.getId()
                .equals(id)) {
                if (entry.getMeta() == TagEntry.WILDCARD || meta == TagEntry.WILDCARD || entry.getMeta() == meta) {
                    if (cacheEntry.getValue()
                        .contains(tagKey)) {
                        return true;
                    }
                }
            }
        }
        return false;
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

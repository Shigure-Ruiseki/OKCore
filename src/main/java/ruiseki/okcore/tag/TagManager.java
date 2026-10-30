package ruiseki.okcore.tag;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.DataManager;
import ruiseki.okcore.data.MultiJsonResourceReloadListener;
import ruiseki.okcore.helper.TagHelpers;
import ruiseki.okcore.init.IRegistry;

/**
 * Tag storage without TagEntry. Wire representation uses registry names; runtime
 * item/block lookups use packed instance IDs and metadata.
 */
public class TagManager extends MultiJsonResourceReloadListener implements IRegistry {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .disableHtmlEscaping()
        .create();
    private static final TagManager INSTANCE = new TagManager();
    public static final int WILDCARD = OreDictionary.WILDCARD_VALUE;

    // Portable representation. Used by network synchronization and re-baking.
    private volatile Snapshot snapshot = new Snapshot();

    private static final class Snapshot {

        final Map<TagKey<?>, LongSet> packedMembers = new HashMap<>();
        final Map<ResourceKey<?>, Long2ObjectMap<Set<TagKey<?>>>> packedReverse = new HashMap<>();
        Map<TagKey<?>, Set<String>> tagValues = Collections.emptyMap();
        final Map<ResourceKey<?>, Long2ObjectMap<Set<TagKey<?>>>> effectiveTags = new HashMap<>();
        final Map<TagKey<?>, Set<ResourceLocation>> namedMembers = new HashMap<>();
        final Map<ResourceKey<?>, Map<ResourceLocation, Set<TagKey<?>>>> namedReverse = new HashMap<>();
    }

    public TagManager() {
        super(GSON, "tags");
    }

    public static TagManager getManager() {
        return INSTANCE;
    }

    @Override
    protected void apply(Map<ResourceLocation, List<JsonElement>> data, DataManager manager) {
        Map<TagKey<?>, Set<String>> raw = new HashMap<>();
        for (Map.Entry<ResourceLocation, List<JsonElement>> file : data.entrySet()) {
            ResourceLocation path = file.getKey();
            String full = path.getResourcePath();
            int slash = full.indexOf('/');
            if (slash <= 0 || slash == full.length() - 1) continue;
            ResourceKey<?> registry = ResourceKey.createRegistryKey(new ResourceLocation(full.substring(0, slash)));
            TagKey<?> tag = TagKey
                .create(registry, new ResourceLocation(path.getResourceDomain(), full.substring(slash + 1)));
            Set<String> values = raw.computeIfAbsent(tag, k -> new LinkedHashSet<>());
            for (JsonElement element : file.getValue()) {
                try {
                    if (!element.isJsonObject()) continue;
                    JsonObject object = element.getAsJsonObject();
                    if (object.has("replace") && object.get("replace")
                        .getAsBoolean()) values.clear();
                    if (!object.has("values") || !object.get("values")
                        .isJsonArray()) continue;
                    JsonArray array = object.getAsJsonArray("values");
                    for (JsonElement value : array) {
                        if (value.isJsonPrimitive() && value.getAsJsonPrimitive()
                            .isString()) {
                            values.add(value.getAsString());
                        }
                    }
                } catch (RuntimeException e) {
                    OKCore.okLog(Level.ERROR, "Failed to parse tag {}: {}", path, e.getMessage());
                }
            }
        }

        Map<TagKey<?>, Set<String>> flattened = new HashMap<>();
        for (TagKey<?> tag : raw.keySet()) {
            Set<String> resolved = new LinkedHashSet<>();
            flatten(tag, raw, resolved, new LinkedHashSet<>());
            flattened.put(tag, Collections.unmodifiableSet(resolved));
        }
        install(flattened);
        OKCore.okLog(Level.INFO, "Loaded {} tags", flattened.size());
    }

    private static Set<TagKey<?>> resolveTags(Snapshot snapshot, ResourceKey<?> registry, long packed) {
        Long2ObjectMap<Set<TagKey<?>>> reverse = snapshot.packedReverse.get(registry);
        if (reverse == null) return Collections.emptySet();

        Set<TagKey<?>> exact = reverse.get(packed);

        if (TagHelpers.getMetaFromLong(packed) == WILDCARD) {
            return exact == null ? Collections.emptySet() : exact;
        }

        long wildcard = TagHelpers.pack(TagHelpers.getIDFromLong(packed), WILDCARD);

        Set<TagKey<?>> extra = reverse.get(wildcard);

        if (exact == null) return extra == null ? Collections.emptySet() : extra;
        if (extra == null || extra.isEmpty()) return exact;

        Set<TagKey<?>> merged = new LinkedHashSet<>(exact);
        merged.addAll(extra);
        return Collections.unmodifiableSet(merged);
    }

    private void flatten(TagKey<?> tag, Map<TagKey<?>, Set<String>> raw, Set<String> result, Set<TagKey<?>> visiting) {
        if (!visiting.add(tag)) {
            OKCore.okLog(Level.WARN, "Cyclic tag reference: {}", tag);
            return;
        }
        Set<String> values = raw.get(tag);
        if (values != null) {
            for (String value : values) {
                if (value.startsWith("#")) {
                    ResourceLocation childId = parseLocation(value.substring(1));
                    if (childId != null) flatten(TagKey.create(tag.registry(), childId), raw, result, visiting);
                } else if (!value.isEmpty()) {
                    result.add(value);
                }
            }
        }
        visiting.remove(tag);
    }

    private static ResourceLocation parseLocation(String input) {
        try {
            if (input == null || input.isEmpty()) return null;
            return new ResourceLocation(input);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static final class ParsedValue {

        final ResourceLocation id;
        final int meta;

        ParsedValue(ResourceLocation id, int meta) {
            this.id = id;
            this.meta = meta;
        }
    }

    // Format: namespace:path[:meta], namespace:path:*, namespace:path:#wildcard
    private static ParsedValue parseValue(String value) {
        if (value == null) return null;
        int first = value.indexOf(':');
        if (first <= 0 || first == value.length() - 1) return null;
        int second = value.indexOf(':', first + 1);
        try {
            ResourceLocation id = second < 0
                ? new ResourceLocation(value.substring(0, first), value.substring(first + 1))
                : new ResourceLocation(value.substring(0, first), value.substring(first + 1, second));
            int meta = 0;
            if (second >= 0) {
                String suffix = value.substring(second + 1);
                meta = suffix.equals("*") || suffix.equalsIgnoreCase("#wildcard") ? WILDCARD : Integer.parseInt(suffix);
            }
            return new ParsedValue(id, meta);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private synchronized void install(Map<TagKey<?>, Set<String>> values) {
        Map<TagKey<?>, Set<String>> immutable = new HashMap<>();
        Snapshot next = new Snapshot();
        for (Map.Entry<TagKey<?>, Set<String>> e : values.entrySet()) {
            TagKey<?> tag = e.getKey();
            if (tag == null || e.getValue() == null) continue;
            Set<String> copy = Collections.unmodifiableSet(new LinkedHashSet<>(e.getValue()));
            immutable.put(tag, copy);
            ResourceKey<?> registry = tag.registry();
            for (String name : copy) {
                ParsedValue parsed = parseValue(name);
                if (parsed == null) continue;
                if (registry == Registries.ITEM || registry == Registries.BLOCK) {
                    long packed;
                    if (registry == Registries.ITEM) {
                        Item item = GameData.getItemRegistry()
                            .getObject(parsed.id.toString());
                        if (item == null) continue;
                        packed = TagHelpers.pack(item, parsed.meta);
                    } else {
                        Block block = GameData.getBlockRegistry()
                            .getObject(parsed.id.toString());
                        if (block == null) continue;
                        packed = TagHelpers.pack(block, parsed.meta);
                    }
                    next.packedMembers.computeIfAbsent(tag, k -> new LongOpenHashSet())
                        .add(packed);
                    Long2ObjectMap<Set<TagKey<?>>> reverse = next.packedReverse
                        .computeIfAbsent(registry, k -> new Long2ObjectOpenHashMap<>());
                    reverse.computeIfAbsent(packed, k -> new LinkedHashSet<>())
                        .add(tag);
                } else {
                    next.namedMembers.computeIfAbsent(tag, k -> new LinkedHashSet<>())
                        .add(parsed.id);
                    next.namedReverse.computeIfAbsent(registry, k -> new HashMap<>())
                        .computeIfAbsent(parsed.id, k -> new LinkedHashSet<>())
                        .add(tag);
                }
            }
        }
        next.tagValues = Collections.unmodifiableMap(immutable);

        // Freeze all backing collections before publishing the new snapshot.
        next.packedMembers.replaceAll((key, members) -> LongSets.unmodifiable(members));
        next.namedMembers.replaceAll((key, members) -> Collections.unmodifiableSet(members));
        for (Map<ResourceLocation, Set<TagKey<?>>> byName : next.namedReverse.values()) {
            byName.replaceAll((key, tags) -> Collections.unmodifiableSet(tags));
        }
        for (Map.Entry<ResourceKey<?>, Long2ObjectMap<Set<TagKey<?>>>> registryEntry : next.packedReverse.entrySet()) {
            Long2ObjectMap<Set<TagKey<?>>> reverse = registryEntry.getValue();
            for (Long2ObjectMap.Entry<Set<TagKey<?>>> e : reverse.long2ObjectEntrySet()) {
                e.setValue(Collections.unmodifiableSet(e.getValue()));
            }

            // Precompute exact + wildcard unions for explicitly indexed variants.
            Long2ObjectMap<Set<TagKey<?>>> effective = new Long2ObjectOpenHashMap<>();
            for (Long2ObjectMap.Entry<Set<TagKey<?>>> e : reverse.long2ObjectEntrySet()) {
                effective.put(e.getLongKey(), resolveTags(next, registryEntry.getKey(), e.getLongKey()));
            }
            next.effectiveTags.put(registryEntry.getKey(), effective);
        }

        this.snapshot = next;
    }

    /** Portable, flattened tag values; safe to send as names over the network. */
    public Map<TagKey<?>, Set<String>> getTags() {
        return snapshot.tagValues;
    }

    public Set<String> getEntries(TagKey<?> tag) {
        return snapshot.tagValues.getOrDefault(tag, Collections.emptySet());
    }

    /** Packed members only for ITEM and BLOCK registries. */
    public LongSet getPackedMembers(TagKey<?> tag) {
        LongSet set = snapshot.packedMembers.get(tag);
        return set == null ? LongSets.EMPTY_SET : set;
    }

    public boolean hasTag(ResourceKey<?> registry, long packed, TagKey<?> tag) {
        if (registry == null || tag == null || registry != tag.registry()) return false;
        Long2ObjectMap<Set<TagKey<?>>> reverse = snapshot.packedReverse.get(registry);
        if (reverse == null) return false;
        Set<TagKey<?>> exact = reverse.get(packed);
        if (exact != null && exact.contains(tag)) return true;
        if (TagHelpers.getMetaFromLong(packed) == WILDCARD) return false;
        long wildcard = TagHelpers.pack(TagHelpers.getIDFromLong(packed), WILDCARD);
        Set<TagKey<?>> all = reverse.get(wildcard);
        return all != null && all.contains(tag);
    }

    @SuppressWarnings("unchecked")
    public <T> Set<TagKey<T>> getTags(ResourceKey<T> registry, long packed) {
        Snapshot current = snapshot;
        Long2ObjectMap<Set<TagKey<?>>> effective = current.effectiveTags.get(registry);
        if (effective == null) return Collections.emptySet();
        Set<TagKey<?>> cached = effective.get(packed);
        if (cached != null) return (Set<TagKey<T>>) (Set<?>) cached;

        // Unknown metadata can still inherit wildcard tags, without caching
        // every possible runtime metadata value indefinitely.
        if (TagHelpers.getMetaFromLong(packed) == WILDCARD) return Collections.emptySet();
        long wildcard = TagHelpers.pack(TagHelpers.getIDFromLong(packed), WILDCARD);
        Set<TagKey<?>> inherited = effective.get(wildcard);
        return inherited == null ? Collections.emptySet() : (Set<TagKey<T>>) (Set<?>) inherited;
    }

    public boolean hasTag(ResourceKey<?> registry, ResourceLocation id, int meta, TagKey<?> tag) {
        if (registry == null || id == null || tag == null || registry != tag.registry()) return false;
        if (registry == Registries.ITEM) {
            Item item = GameData.getItemRegistry()
                .getObject(id.toString());
            return item != null && hasTag(registry, TagHelpers.pack(item, meta), tag);
        }
        if (registry == Registries.BLOCK) {
            Block block = GameData.getBlockRegistry()
                .getObject(id.toString());
            return block != null && hasTag(registry, TagHelpers.pack(block, meta), tag);
        }
        Set<TagKey<?>> tags = snapshot.namedReverse.getOrDefault(registry, Collections.emptyMap())
            .get(id);
        return tags != null && tags.contains(tag);
    }

    public <T> Set<TagKey<T>> getTags(ResourceKey<T> registry, ResourceLocation id, int meta) {
        if (registry == null || id == null) return Collections.emptySet();
        if (registry == Registries.ITEM) {
            Item item = GameData.getItemRegistry()
                .getObject(id.toString());
            return item == null ? Collections.emptySet() : getTags(registry, TagHelpers.pack(item, meta));
        }
        if (registry == Registries.BLOCK) {
            Block block = GameData.getBlockRegistry()
                .getObject(id.toString());
            return block == null ? Collections.emptySet() : getTags(registry, TagHelpers.pack(block, meta));
        }
        Set<TagKey<?>> tags = snapshot.namedReverse.getOrDefault(registry, Collections.emptyMap())
            .get(id);
        if (tags == null) return Collections.emptySet();
        // Named reverse sets are already immutable inside the snapshot.
        @SuppressWarnings("unchecked")
        Set<TagKey<T>> typed = (Set<TagKey<T>>) (Set<?>) tags;
        return typed;
    }

    public Set<ResourceLocation> getNamedMembers(TagKey<?> tag) {
        Set<ResourceLocation> members = snapshot.namedMembers.get(tag);
        return members == null ? Collections.emptySet() : members;
    }

    @SideOnly(Side.CLIENT)
    public void replaceTags(Map<TagKey<?>, Set<String>> serverTags) {
        install(serverTags);
    }
}

package ruiseki.okcore.tag;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.tag.entry.TagEntry;

public class TagManager {

    private static TagManager instance;

    private Map<TagKey<?>, Set<TagEntry<?>>> tagToEntriesMap = ImmutableMap.of();
    private Map<TagEntry<?>, Set<TagKey<?>>> entryToTagsCache = ImmutableMap.of();

    public TagManager() {}

    public static TagManager getManager() {
        if (instance == null) {
            instance = new TagManager();
        }
        return instance;
    }

    public void clearTags() {
        this.tagToEntriesMap = ImmutableMap.of();
        this.entryToTagsCache = ImmutableMap.of();
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
        return elements != null ? Collections.unmodifiableSet(elements) : Collections.emptySet();
    }

    public void addTags(Map<TagKey<?>, Set<TagEntry<?>>> incomingTags) {
        if (incomingTags == null) return;

        Map<TagKey<?>, Set<TagEntry<?>>> mutableMap = new HashMap<>();
        for (Map.Entry<TagKey<?>, Set<TagEntry<?>>> entry : this.tagToEntriesMap.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                mutableMap.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }
        }

        for (Map.Entry<TagKey<?>, Set<TagEntry<?>>> entry : incomingTags.entrySet()) {
            TagKey<?> tagKey = entry.getKey();
            Set<TagEntry<?>> wrappers = entry.getValue();
            if (tagKey != null && wrappers != null) {
                mutableMap.computeIfAbsent(tagKey, k -> new HashSet<>())
                    .addAll(wrappers);
            }
        }
        this.tagToEntriesMap = Helpers.copyToMSImmutable(mutableMap);
        this.bakeCache();
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
        this.tagToEntriesMap = Helpers.copyToMSImmutable(map);
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
        this.entryToTagsCache = ImmutableMap.copyOf(tempCache);
    }
}

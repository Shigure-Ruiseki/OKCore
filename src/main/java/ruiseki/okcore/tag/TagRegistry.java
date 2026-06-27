package ruiseki.okcore.tag;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ruiseki.okcore.data.loader.tags.TagHolder;
import ruiseki.okcore.tag.entry.TagEntry;

public class TagRegistry {

    private static final Map<TagKey<?>, TagHolder<?>> TAG_HOLDERS = new HashMap<>();

    public static void addHolder(TagKey<?> tagKey, TagHolder<?> holder) {
        if (holder == null || holder.values()
            .isEmpty()) return;
        TAG_HOLDERS.put(tagKey, holder);
    }

    @SuppressWarnings("unchecked")
    public static void processHolders() {
        if (TAG_HOLDERS.isEmpty()) return;

        TagManager manager = TagManager.getManager();
        Map<TagKey<?>, Set<TagEntry<?>>> finalTagsMap = new HashMap<>();

        for (Map.Entry<TagKey<?>, TagHolder<?>> entry : TAG_HOLDERS.entrySet()) {
            TagKey<?> tagKey = entry.getKey();
            TagHolder<?> holder = entry.getValue();

            if (holder.replace() || !finalTagsMap.containsKey(tagKey)) {
                finalTagsMap.put(tagKey, new HashSet<>(holder.values()));
            } else {
                Set<TagEntry<?>> accumulatedEntries = finalTagsMap.get(tagKey);
                accumulatedEntries.addAll((Collection<TagEntry<?>>) (Collection<?>) holder.values());
            }
        }

        manager.clearTags();
        manager.addTags(finalTagsMap);
        TAG_HOLDERS.clear();
    }
}

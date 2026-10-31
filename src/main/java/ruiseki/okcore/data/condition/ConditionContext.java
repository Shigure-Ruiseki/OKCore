
package ruiseki.okcore.data.condition;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;

public class ConditionContext implements ICondition.IContext {

    private final TagManager tagManager;

    private Map<ResourceLocation, Map<ResourceLocation, Collection<?>>> loadedTags;

    public ConditionContext(TagManager tagManager) {
        this.tagManager = tagManager;
    }

    @Override
    public Map<ResourceLocation, Collection<?>> getAllTags(ResourceLocation registryKey) {
        if (this.loadedTags == null) {
            Map<TagKey<?>, Set<String>> allTags = this.tagManager.getTags();

            if (allTags.isEmpty()) {
                throw new IllegalStateException("Tags have not been loaded yet.");
            }

            Map<ResourceLocation, Map<ResourceLocation, Collection<?>>> tempMap = new Object2ObjectOpenHashMap<>();

            for (Map.Entry<TagKey<?>, Set<String>> entry : allTags.entrySet()) {
                TagKey<?> tagKey = entry.getKey();
                Set<String> entries = entry.getValue();

                if (tagKey == null || entries == null) continue;

                ResourceLocation regId = tagKey.registry()
                    .location();
                ResourceLocation tagId = tagKey.location();

                Map<ResourceLocation, Collection<?>> tagMap = tempMap
                    .computeIfAbsent(regId, k -> new Object2ObjectOpenHashMap<>());

                tagMap.put(tagId, Collections.unmodifiableSet(entries));
            }

            Map<ResourceLocation, Map<ResourceLocation, Collection<?>>> builder = new Object2ObjectOpenHashMap<>();

            tempMap.forEach((key, value) -> builder.put(key, Collections.unmodifiableMap(value)));

            this.loadedTags = Collections.unmodifiableMap(builder);
        }

        return this.loadedTags.getOrDefault(registryKey, Collections.emptyMap());
    }
}

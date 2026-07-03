package ruiseki.okcore.data.condition;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

public interface ICondition {

    ResourceLocation getID();

    boolean test(IContext context);

    interface IContext {

        IContext EMPTY = new IContext() {

            @Override
            public Map<ResourceLocation, Collection<?>> getAllTags(ResourceLocation registryKey) {
                return Collections.emptyMap();
            }
        };

        Map<ResourceLocation, Collection<?>> getAllTags(ResourceLocation registryKey);

        default Collection<?> getTag(ResourceLocation registryKey, ResourceLocation tagLocation) {
            return this.getAllTags(registryKey)
                .getOrDefault(tagLocation, Collections.emptyList());
        };

    }
}

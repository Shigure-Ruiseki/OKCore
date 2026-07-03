package ruiseki.okcore.tag.entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

public class EntityTagEntry extends TagEntry<Entity> {

    public EntityTagEntry(Entity entity) {
        super((entity != null) ? new ResourceLocation(EntityList.getEntityString(entity)) : null, WILDCARD);
    }

    public EntityTagEntry(ResourceLocation id, int meta) {
        super(id, meta);
    }

    @Override
    public Class<Entity> getType() {
        return Entity.class;
    }

    public static class Serializer implements ITagEntrySerializer<Entity, EntityTagEntry> {

        public static final EntityTagEntry.Serializer INSTANCE = new EntityTagEntry.Serializer();

        @Override
        public String getKey() {
            return "entity_type";
        }

        @Override
        public EntityTagEntry read(ResourceLocation id, int meta) {
            return new EntityTagEntry(id, meta);
        }
    }
}

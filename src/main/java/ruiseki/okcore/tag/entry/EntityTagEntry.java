package ruiseki.okcore.tag.entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.ResourceKey;

@TagData
public class EntityTagEntry extends TagEntry<Entity> {

    public EntityTagEntry() {
        super(null, WILDCARD);
    }

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

    @Override
    public String getKey() {
        return "entity_type";
    }

    @Override
    public ResourceKey<?> getRegistryKey() {
        return Registries.ENTITY_TYPE;
    }

    @Override
    public TagEntry<Entity> create(ResourceLocation id, int meta) {
        return new EntityTagEntry(id, meta);
    }

    @Override
    public Entity to() {
        return null;
    }
}

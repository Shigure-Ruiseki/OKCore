package ruiseki.okcore.tag.entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.ResourceKey;

@TagData
public class ItemTagEntry extends TagEntry<ItemStack> {

    public ItemTagEntry() {
        super(null, 0);
    }

    public ItemTagEntry(Item item) {
        super((item != null) ? new ResourceLocation(Item.itemRegistry.getNameForObject(item)) : null, 0);
    }

    public ItemTagEntry(ItemStack stack) {
        super(
            (stack != null && stack.getItem() != null)
                ? new ResourceLocation(Item.itemRegistry.getNameForObject(stack.getItem()))
                : null,
            (stack != null) ? stack.getItemDamage() : 0);
    }

    public ItemTagEntry(ResourceLocation id, int meta) {
        super(id, meta);
    }

    @Override
    public Class<ItemStack> getType() {
        return ItemStack.class;
    }

    @Override
    public String getKey() {
        return "item";
    }

    @Override
    public ResourceKey<?> getRegistryKey() {
        return Registries.ITEM;
    }

    @Override
    public TagEntry<ItemStack> create(ResourceLocation id, int meta) {
        return new ItemTagEntry(id, meta);
    }

    @Override
    public ItemStack to() {
        if (this.id == null) return null;
        Item item = (Item) Item.itemRegistry.getObject(this.id.toString());
        if (item == null) return null;
        int finalMeta = (this.meta == WILDCARD) ? 0 : this.meta;
        return new ItemStack(item, 1, finalMeta);
    }
}

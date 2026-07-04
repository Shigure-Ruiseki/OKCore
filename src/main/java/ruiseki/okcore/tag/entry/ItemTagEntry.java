package ruiseki.okcore.tag.entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemTagEntry extends TagEntry<ItemStack> {

    public ItemTagEntry(ResourceLocation id, int meta) {
        super(id, meta);
    }

    @Override
    public Class<ItemStack> getType() {
        return ItemStack.class;
    }

    @Override
    public ItemStack get() {
        Item item = (Item) Item.itemRegistry.getObject(this.id.toString());
        if (item == null) return null;
        int itemMeta = this.meta == WILDCARD ? 0 : this.meta;
        return new ItemStack(item, 1, itemMeta);
    }

    public static class Serializer implements ITagEntrySerializer<ItemStack, ItemTagEntry> {

        public static final ItemTagEntry.Serializer INSTANCE = new ItemTagEntry.Serializer();

        @Override
        public String getKey() {
            return "item";
        }

        @Override
        public ItemTagEntry read(ResourceLocation id, int meta) {
            return new ItemTagEntry(id, meta);
        }
    }
}

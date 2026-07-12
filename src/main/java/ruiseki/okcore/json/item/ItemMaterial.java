package ruiseki.okcore.json.item;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.helper.TagHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.TagEntry;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;

public class ItemMaterial extends IngredientMaterial {

    private Item item;
    private String name;
    private String tag;
    private int amount = 1;
    private int meta = 0;
    private NBTTagCompound nbt;

    @Override
    public void read(JsonObject json) {
        this.name = getString(json, "item", null);
        this.item = null;

        String tagName = getString(json, "tag", null);
        if (tagName == null || tagName.isEmpty()) tagName = getString(json, "ore", null);
        this.tag = (tagName != null && !tagName.isEmpty()) ? tagName : null;

        this.amount = getInt(json, "amount", 1);
        this.meta = getInt(json, "meta", 0);
        this.nbt = json.has("nbt") ? GsonHelpers.jsonToNBT(json.getAsJsonObject("nbt")) : null;

        captureUnknownProperties(json, "item", "tag", "ore", "amount", "meta", "nbt");
    }

    @Override
    public void write(JsonObject json) {
        Item currentItem = getItem();
        if (currentItem != null) {
            json.addProperty(
                "item",
                GameData.getItemRegistry()
                    .getNameForObject(currentItem));
        } else if (this.name != null) {
            json.addProperty("item", this.name);
        }
        if (this.tag != null) json.addProperty("tag", this.tag);
        json.addProperty("amount", this.amount);
        json.addProperty("meta", this.meta);
        if (this.nbt != null && !this.nbt.hasNoTags()) {
            json.add("nbt", GsonHelpers.nbtToJSON(this.nbt));
        }
        writeUnknownProperties(json);
    }

    @Override
    public boolean validate() {
        if (this.name == null && this.tag == null) {
            logValidationError("ItemMaterial item or tag cannot be empty!");
            return false;
        }
        return true;
    }

    public Item getItem() {
        if (this.item == null && this.name != null) {
            this.item = GameData.getItemRegistry()
                .getObject(this.name);
        }
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public int getMeta() {
        return meta;
    }

    public NBTTagCompound getNbt() {
        return nbt;
    }

    public String getTag() {
        return tag;
    }

    @Deprecated
    public String getOre() {
        return getTag();
    }

    public void fromStack(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            this.item = null;
            this.name = null;
            this.tag = null;
            this.amount = 0;
            this.meta = 0;
            this.nbt = null;
            return;
        }

        this.item = stack.getItem();
        this.name = GameData.getItemRegistry()
            .getNameForObject(this.item);
        this.amount = stack.stackSize;
        this.meta = stack.getItemDamage();
        this.nbt = stack.hasTagCompound() ? (NBTTagCompound) stack.getTagCompound()
            .copy() : null;
        this.tag = null;
        this.unknownProperties.clear();
    }

    public ItemStack toStack() {
        int count = this.amount > 0 ? this.amount : 1;

        if (this.tag != null && !this.tag.isEmpty()) {
            if (this.tag.startsWith("#") || this.tag.contains(":")) {
                String tagIdentifier = this.tag.startsWith("#") ? this.tag.substring(1) : this.tag;
                return resolveFromTag(tagIdentifier, count, this.nbt);
            } else {
                return resolveFromOre(this.tag, count, this.nbt);
            }
        }

        Item item = getItem();
        if (item == null) return null;
        try {
            ItemStack stack = new ItemStack(item, count, this.meta);

            if (this.nbt != null) {
                stack.setTagCompound((NBTTagCompound) this.nbt.copy());
            }

            return stack;
        } catch (Throwable t) {
            return null;
        }
    }

    private static ItemStack resolveFromTag(String tagStr, int count, NBTTagCompound nbt) {
        if (tagStr == null || tagStr.isEmpty()) return null;
        try {
            ResourceLocation loc = new ResourceLocation(tagStr);
            TagKey<ItemStack> tagKey = TagKey.create(Registries.ITEM, loc);

            Set<TagEntry> entries = TagManager.getManager()
                .getEntries(tagKey);
            if (entries == null || entries.isEmpty()) return null;

            TagEntry firstEntry = entries.iterator()
                .next();
            Item item = GameData.getItemRegistry()
                .getObject(
                    firstEntry.id()
                        .toString());
            if (item == null) return null;

            int itemMeta = firstEntry.meta() == TagEntry.WILDCARD ? 0 : firstEntry.meta();
            ItemStack result = new ItemStack(item, count, itemMeta);
            if (nbt != null) {
                result.setTagCompound((NBTTagCompound) nbt.copy());
            }
            return result;
        } catch (Throwable t) {
            return null;
        }
    }

    private static ItemStack resolveFromOre(String ore, int count, NBTTagCompound nbt) {
        if (ore == null || ore.isEmpty()) return null;
        try {
            List<ItemStack> list = OreDictionary.getOres(ore);
            if (list == null || list.isEmpty()) return null;

            ItemStack base = list.getFirst();
            if (base == null) return null;

            ItemStack result = base.copy();
            result.stackSize = count;
            if (nbt != null) {
                result.setTagCompound((NBTTagCompound) nbt.copy());
            }

            return result;
        } catch (Throwable t) {
            return null;
        }
    }

    public static ItemMaterial parseItemStack(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return null;
        }

        ItemMaterial material = new ItemMaterial();
        material.item = stack.getItem();
        material.name = GameData.getItemRegistry()
            .getNameForObject(stack.getItem());
        material.amount = stack.stackSize;
        material.meta = stack.getItemDamage();
        if (stack.hasTagCompound()) {
            material.nbt = (NBTTagCompound) stack.getTagCompound()
                .copy();
        } else {
            material.nbt = null;
        }

        material.tag = null;
        return material;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemMaterial that = (ItemMaterial) o;

        if (amount != that.amount) return false;
        if (meta != that.meta) return false;
        if (!Objects.equals(item, that.item)) return false;
        return Objects.equals(tag, that.tag) && Objects.equals(nbt, that.nbt);
    }

    @Override
    public int hashCode() {
        int result = getItem() != null ? getItem().hashCode() : 0;
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        result = 31 * result + amount;
        result = 31 * result + meta;
        result = 31 * result + (nbt != null ? nbt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (tag != null && !tag.isEmpty()) {
            if (tag.startsWith("#") || tag.contains(":")) {
                String cleanTag = tag.startsWith("#") ? tag.substring(1) : tag;
                return "ItemMaterial[Tag=" + cleanTag + " x" + amount + "]";
            }
            return "ItemMaterial[Ore=" + tag + " x" + amount + "]";
        }
        return "ItemMaterial[Item=" + item + ":" + meta + " x" + amount + (nbt != null ? " (Has NBT)" : "") + "]";
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }

        if (this.tag != null && !this.tag.isEmpty()) {
            if (this.tag.startsWith("#") || this.tag.contains(":")) {
                String tagIdentifier = this.tag.startsWith("#") ? this.tag.substring(1) : this.tag;
                ResourceLocation loc = new ResourceLocation(tagIdentifier);
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, loc);
                return TagHelpers.hasTag(stack, tagKey);
            }

            List<ItemStack> ores = OreDictionary.getOres(this.tag);
            if (ores != null) {
                for (ItemStack oreStack : ores) {
                    if (OreDictionary.itemMatches(oreStack, stack, false)) {
                        return true;
                    }
                }
            }
            return false;
        }

        Item targetItem = getItem();
        if (targetItem == null || stack.getItem() != targetItem) {
            return false;
        }

        if (this.meta != OreDictionary.WILDCARD_VALUE && this.meta != stack.getItemDamage()) {
            return false;
        }

        return this.nbt == null || this.nbt.equals(stack.getTagCompound());
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer) throws IOException {
        int mode = 0;
        if (this.tag != null && !this.tag.isEmpty()) {
            mode = (this.tag.startsWith("#") || this.tag.contains(":")) ? 2 : 1;
        }

        buffer.writeInt(mode);

        if (mode == 2 || mode == 1) {
            buffer.writeString(this.tag);
        } else {
            String registryName = this.name;
            if (registryName == null && getItem() != null) {
                registryName = GameData.getItemRegistry()
                    .getNameForObject(this.item);
            }
            buffer.writeString(registryName != null ? registryName : "");
            buffer.writeInt(this.meta);
        }

        buffer.writeInt(this.amount);

        boolean hasNbt = (this.nbt != null && !this.nbt.hasNoTags());
        buffer.writeBoolean(hasNbt);
        if (hasNbt) {
            buffer.writeNBTTagCompoundToBuffer(this.nbt);
        }
    }

    @Override
    public void fromNetwork(ExtendedBuffer buffer) throws IOException {
        this.item = null;
        this.unknownProperties.clear();

        int mode = buffer.readInt();
        if (mode == 2 || mode == 1) {
            this.tag = buffer.readString();
            this.name = null;
        } else {
            this.name = buffer.readString();
            if (this.name.isEmpty()) this.name = null;
            this.meta = buffer.readInt();
            this.tag = null;
        }

        this.amount = buffer.readInt();

        boolean hasNbt = buffer.readBoolean();
        if (hasNbt) {
            this.nbt = buffer.readNBTTagCompoundFromBuffer();
        } else {
            this.nbt = null;
        }
    }
}

package ruiseki.okcore.json.item;

import java.util.List;
import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.okcore.helper.JsonNBTHelpers;
import ruiseki.okcore.json.AbstractJsonMaterial;

public class ItemMaterial extends AbstractJsonMaterial {

    private Item item;
    private String ore;
    private int amount = 1;
    private int meta = 0;
    private NBTTagCompound nbt;

    @Override
    public void read(JsonObject json) {
        String itemName = getString(json, "item", null);
        this.item = GameData.getItemRegistry()
            .getObject(itemName);
        String oreName = getString(json, "ore", null);
        this.ore = (oreName != null && !oreName.isEmpty()) ? oreName : null;
        this.amount = getInt(json, "amount", 1);
        this.meta = getInt(json, "meta", 0);
        this.nbt = json.has("nbt") ? JsonNBTHelpers.jsonToNBT(json.getAsJsonObject("nbt")) : null;
        captureUnknownProperties(json, "item", "ore", "amount", "meta", "nbt");
    }

    @Override
    public void write(JsonObject json) {
        if (this.item != null) json.addProperty(
            "item",
            GameData.getBlockRegistry()
                .getNameForObject(item));
        if (this.ore != null) json.addProperty("ore", this.ore);
        json.addProperty("amount", this.amount);
        json.addProperty("meta", this.meta);
        if (this.nbt != null && !this.nbt.hasNoTags()) {
            json.add("nbt", JsonNBTHelpers.nbtToJSON(this.nbt));
        }
        writeUnknownProperties(json);
    }

    @Override
    public boolean validate() {
        if (item == null && ore == null) {
            logValidationError("ItemMaterial item or ore cannot be empty!");
            return false;
        }
        return toStack() != null;
    }

    public Item getItem() {
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

    public String getOre() {
        return ore;
    }

    public void fromStack(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            this.item = null;
            this.ore = null;
            this.amount = 0;
            this.meta = 0;
            this.nbt = null;
            return;
        }

        this.item = stack.getItem();
        this.amount = stack.stackSize;
        this.meta = stack.getItemDamage();
        this.nbt = stack.hasTagCompound() ? (NBTTagCompound) stack.getTagCompound()
            .copy() : null;
        this.ore = null;
        this.unknownProperties.clear();
    }

    public ItemStack toStack() {
        int count = this.amount > 0 ? this.amount : 1;
        if (this.ore != null && !this.ore.isEmpty()) {
            return resolveFromOre(this.ore, count, this.nbt);
        }

        if (this.item == null) return null;
        try {
            ItemStack stack = new ItemStack(this.item, count, this.meta);

            if (this.nbt != null) {
                stack.setTagCompound((NBTTagCompound) this.nbt.copy());
            }

            return stack;
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
        material.amount = stack.stackSize;
        material.meta = stack.getItemDamage();
        if (stack.hasTagCompound()) {
            material.nbt = (NBTTagCompound) stack.getTagCompound()
                .copy();
        } else {
            material.nbt = null;
        }

        material.ore = null;
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
        if (!Objects.equals(ore, that.ore)) return false;
        return Objects.equals(nbt, that.nbt);
    }

    @Override
    public int hashCode() {
        int result = item != null ? item.hashCode() : 0;
        result = 31 * result + (ore != null ? ore.hashCode() : 0);
        result = 31 * result + amount;
        result = 31 * result + meta;
        result = 31 * result + (nbt != null ? nbt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (ore != null && !ore.isEmpty()) {
            return "ItemMaterial[Ore=" + ore + " x" + amount + "]";
        }
        return "ItemMaterial[Item=" + item + ":" + meta + " x" + amount + (nbt != null ? " (Has NBT)" : "") + "]";
    }
}

package ruiseki.okcore.json;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;

/**
 * Material representation of an ItemStack in JSON.
 */
public class ItemMaterial extends AbstractJsonMaterial {

    public String name;
    public String ore;
    public int amount = 1;
    public int meta = 0;
    public NBTTagCompound nbt;

    public ItemMaterial() {}

    public ItemMaterial(ItemStack stack) {
        ItemJson internal = ItemJson.parseItemStack(stack);
        if (internal != null) {
            this.name = internal.name;
            this.amount = internal.amount;
            this.meta = internal.meta;

            if (stack.hasTagCompound()) {
                this.nbt = (NBTTagCompound) stack.getTagCompound()
                    .copy();
            }
        }
    }

    @Override
    public void read(com.google.gson.JsonElement json) {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive()
            .isString()) {

            ItemJson parsed = ItemJson.parseItemString(json.getAsString());
            if (parsed != null) {
                this.name = parsed.name;
                this.ore = parsed.ore;
                this.amount = parsed.amount;
                this.meta = parsed.meta;
                this.nbt = parsed.nbt;
            }

        } else if (json.isJsonObject()) {
            read(json.getAsJsonObject());
        }
    }

    @Override
    public void read(JsonObject json) {
        this.name = getString(json, "name", null);
        this.ore = getString(json, "ore", null);
        this.amount = getInt(json, "amount", 1);
        this.meta = getInt(json, "meta", 0);

        if (json.has("nbt")) {
            try {
                this.nbt = parseNBT(
                    json.get("nbt")
                        .getAsString());
            } catch (Exception ignored) {
                this.nbt = null;
            }
        }
    }

    @Override
    public void write(JsonObject json) {
        if (name != null) json.addProperty("name", name);
        if (ore != null) json.addProperty("ore", ore);
        if (amount != 1) json.addProperty("amount", amount);
        if (meta != 0) json.addProperty("meta", meta);

        if (nbt != null) {
            json.addProperty("nbt", nbt.toString());
        }
    }

    @Override
    public Object get(String key) {
        if ("name".equals(key)) return name;
        if ("ore".equals(key)) return ore;
        if ("amount".equals(key)) return amount;
        if ("meta".equals(key)) return meta;
        if ("nbt".equals(key)) return nbt;
        return null;
    }

    @Override
    public void set(String key, Object value) {
        if ("name".equals(key)) this.name = (String) value;
        if ("ore".equals(key)) this.ore = (String) value;

        if ("amount".equals(key))
            this.amount = value instanceof Integer ? (Integer) value : Integer.parseInt(value.toString());

        if ("meta".equals(key))
            this.meta = value instanceof Integer ? (Integer) value : Integer.parseInt(value.toString());

        if ("nbt".equals(key)) {
            if (value instanceof NBTTagCompound) {
                this.nbt = (NBTTagCompound) value;
            } else if (value instanceof String) {
                try {
                    this.nbt = parseNBT((String) value);
                } catch (Exception ignored) {
                    this.nbt = null;
                }
            }
        }
    }

    public ItemStack resolve() {
        return ItemJson.resolveItemStack(toItemJson());
    }

    public ItemJson toItemJson() {
        ItemJson internal = new ItemJson();
        internal.name = this.name;
        internal.ore = this.ore;
        internal.amount = this.amount;
        internal.meta = this.meta;
        internal.nbt = this.nbt;
        return internal;
    }

    public static ItemMaterial parseItemStack(ItemStack stack) {
        if (stack == null) return null;
        return new ItemMaterial(stack);
    }

    public static ItemStack resolveItemStack(ItemMaterial mat) {
        return mat != null ? mat.resolve() : null;
    }

    private static NBTTagCompound parseNBT(String s) throws NBTException {
        NBTBase base = JsonToNBT.func_150315_a(s);
        if (base instanceof NBTTagCompound) {
            return (NBTTagCompound) base;
        }
        return new NBTTagCompound();
    }
}

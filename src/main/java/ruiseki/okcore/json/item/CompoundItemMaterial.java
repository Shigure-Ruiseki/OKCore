package ruiseki.okcore.json.item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.helper.TagHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.TagKey;

public class CompoundItemMaterial extends IngredientMaterial {

    private ItemMaterial[] materials = new ItemMaterial[0];

    public CompoundItemMaterial() {}

    public CompoundItemMaterial(ItemMaterial... materials) {
        if (materials != null) {
            this.materials = materials;
        }
    }

    public List<ItemMaterial> getMaterials() {
        return Collections.unmodifiableList(Arrays.asList(this.materials));
    }

    public boolean isEmpty() {
        return this.materials == null || this.materials.length == 0;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        for (ItemMaterial mat : this.materials) {
            if (mat.test(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void read(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            this.materials = new ItemMaterial[0];
            return;
        }

        List<ItemMaterial> tempList = new ArrayList<>();

        if (element.isJsonObject()) {
            addMaterialIfValid(element.getAsJsonObject(), tempList);
        } else if (element.isJsonArray()) {
            for (JsonElement sub : element.getAsJsonArray()) {
                if (sub.isJsonObject()) {
                    addMaterialIfValid(sub.getAsJsonObject(), tempList);
                }
            }
        }

        this.materials = tempList.toArray(new ItemMaterial[0]);
    }

    @Override
    public void read(JsonObject json) {
        read((JsonElement) json);
    }

    @Override
    public void write(JsonObject json) {
        if (this.materials.length == 1 && this.materials[0] != null) {
            this.materials[0].write(json);
        }
    }

    private void addMaterialIfValid(JsonObject jsonObject, List<ItemMaterial> tempList) {
        ItemMaterial mat = new ItemMaterial();
        mat.read(jsonObject);
        if (mat.validate()) {
            tempList.add(mat);
        }
    }

    public List<ItemStack> toStacks() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemStack> displayStacks = new ArrayList<>();
        for (ItemMaterial mat : this.materials) {
            if (mat == null) continue;

            String tagStr = mat.getTag();
            if (tagStr != null && !tagStr.isEmpty()) {

                if (tagStr.startsWith("#") || tagStr.contains(":")) {
                    try {
                        String tagIdentifier = tagStr.startsWith("#") ? tagStr.substring(1) : tagStr;

                        ResourceLocation loc = new ResourceLocation(tagIdentifier);
                        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, loc);

                        List<ItemStack> stacks = TagHelpers.toItemStacks(tagKey);
                        if (!stacks.isEmpty()) {
                            for (ItemStack stack : stacks) {
                                ItemStack copy = stack.copy();
                                copy.stackSize = mat.getAmount();
                                if (mat.getNbt() != null) {
                                    copy.setTagCompound(
                                        (NBTTagCompound) mat.getNbt()
                                            .copy());
                                }
                                displayStacks.add(copy);
                            }
                        }
                    } catch (Throwable ignored) {}
                } else {
                    List<ItemStack> ores = OreDictionary.getOres(tagStr);
                    if (ores != null) {
                        for (ItemStack oreStack : ores) {
                            if (oreStack != null) {
                                ItemStack copy = oreStack.copy();
                                copy.stackSize = mat.getAmount();

                                if (mat.getNbt() != null) {
                                    copy.setTagCompound(
                                        (NBTTagCompound) mat.getNbt()
                                            .copy());
                                }
                                displayStacks.add(copy);
                            }
                        }
                    }
                }
            } else {
                ItemStack stack = mat.toStack();
                if (stack != null) {
                    displayStacks.add(stack);
                }
            }
        }
        return displayStacks;
    }

    @Override
    public boolean validate() {
        if (this.isEmpty()) {
            logValidationError("CompoundItemMaterial must contain at least one valid ItemMaterial!");
            return false;
        }
        return true;
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer) throws IOException {
        buffer.writeInt(this.materials.length);
        for (ItemMaterial mat : this.materials) {
            if (mat != null) {
                mat.toNetwork(buffer);
            }
        }
    }

    @Override
    public void fromNetwork(ExtendedBuffer buffer) throws IOException {
        int size = buffer.readInt();
        this.materials = new ItemMaterial[size];
        for (int i = 0; i < size; i++) {
            ItemMaterial mat = new ItemMaterial();
            mat.fromNetwork(buffer);
            this.materials[i] = mat;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundItemMaterial that = (CompoundItemMaterial) o;
        return Arrays.equals(this.materials, that.materials);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.materials);
    }

    @Override
    public String toString() {
        return "CompoundItemMaterial" + Arrays.toString(this.materials);
    }
}

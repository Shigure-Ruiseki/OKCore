package ruiseki.okcore.recipe.type.crafting.shaped;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.json.AbstractJsonMaterial;
import ruiseki.okcore.json.item.CompoundItemMaterial;
import ruiseki.okcore.json.item.ItemMaterial;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeSerializer;

public class ShapedRecipeSerializer implements IRecipeSerializer<ShapedRecipe> {

    public static final ShapedRecipeSerializer INSTANCE = new ShapedRecipeSerializer();

    @Override
    public ShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
        ItemStack outputStack = null;
        if (json.has("result")) {
            ItemMaterial mat = new ItemMaterial();
            mat.read(json.getAsJsonObject("result"));
            outputStack = mat.toStack();
        }

        if (outputStack == null) {
            OKCore.okLog(Level.ERROR, "Shaped Recipe [{}] failed to generate: 'result' is missing or invalid.", id);
            return null;
        }

        String[] pattern = AbstractJsonMaterial.getStringArray(json, "pattern");
        if (pattern.length == 0) {
            OKCore.okLog(Level.ERROR, "Shaped Recipe [{}] failed to generate: 'pattern' is missing or empty.", id);
            return null;
        }

        Map<Character, CompoundItemMaterial> keyMap = new HashMap<>();
        if (json.has("key") && json.get("key")
            .isJsonObject()) {
            JsonObject keyObj = json.getAsJsonObject("key");
            for (Map.Entry<String, JsonElement> entry : keyObj.entrySet()) {
                char symbol = entry.getKey()
                    .charAt(0);

                CompoundItemMaterial compMaterial = new CompoundItemMaterial();
                compMaterial.read(entry.getValue());

                if (!compMaterial.validate()) {
                    OKCore.okLog(Level.ERROR, "Shaped Recipe [{}] key '{}' has no valid ingredients.", id, symbol);
                    return null;
                }
                keyMap.put(symbol, compMaterial);
            }
        } else {
            OKCore.okLog(Level.ERROR, "Shaped Recipe [{}] is missing a valid 'key' object.", id);
            return null;
        }

        return new ShapedRecipe(id, outputStack, pattern, keyMap);
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, ShapedRecipe recipe) throws IOException {
        buffer.writeItemStackToBuffer(recipe.getRecipeOutput());

        String[] pattern = recipe.getPattern();
        buffer.writeInt(pattern.length);
        for (String s : pattern) {
            buffer.writeString(s);
        }

        Map<Character, CompoundItemMaterial> keyMap = recipe.getKeyMap();
        buffer.writeInt(keyMap.size());
        for (Map.Entry<Character, CompoundItemMaterial> entry : keyMap.entrySet()) {
            buffer.writeChar(entry.getKey());
            entry.getValue()
                .toNetwork(buffer);
        }
    }

    @Override
    public @Nullable ShapedRecipe fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException {
        ItemStack output = buffer.readItemStackFromBuffer();

        int pLen = buffer.readInt();
        String[] pattern = new String[pLen];
        for (int i = 0; i < pLen; i++) {
            pattern[i] = buffer.readString();
        }

        int kLen = buffer.readInt();
        Map<Character, CompoundItemMaterial> keyMap = new HashMap<>();
        for (int i = 0; i < kLen; i++) {
            char key = buffer.readChar();
            CompoundItemMaterial compMaterial = new CompoundItemMaterial();
            compMaterial.fromNetwork(buffer);
            keyMap.put(key, compMaterial);
        }

        return new ShapedRecipe(id, output, pattern, keyMap);
    }
}

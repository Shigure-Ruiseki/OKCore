package ruiseki.okcore.recipe;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public interface IOreDictEntry {

    Map<String, ItemStack> getOreMappings();

    default void registerOreDict() {
        for (Map.Entry<String, ItemStack> entry : getOreMappings().entrySet()) {
            OreDictionary.registerOre(entry.getKey(), entry.getValue());
        }
    }
}

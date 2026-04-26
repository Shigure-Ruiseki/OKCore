package ruiseki.okcore.item;

import net.minecraft.item.Item;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.recipe.IOreDictEntry;

public interface IItem {

    default void init() {
        registerItem();
        registerComponent();
    }

    Item getItem();

    String getName();

    default void registerItem() {
        GameRegistry.registerItem(this.getItem(), getName());
    }

    default void registerComponent() {
        if (this instanceof IOreDictEntry oreDictEntry) oreDictEntry.registerOreDict();
    }
}

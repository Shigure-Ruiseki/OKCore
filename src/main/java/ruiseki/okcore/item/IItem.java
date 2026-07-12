package ruiseki.okcore.item;

import net.minecraft.item.Item;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.recipe.IOreDictEntry;
import ruiseki.okcore.registries.IRegistrable;

public interface IItem extends IRegistrable<Item> {

    @Override
    default void register(String name) {
        get().setUnlocalizedName(name);
        registerItem(name);
        registerComponent(name);
    }

    default void registerItem(String name) {
        GameRegistry.registerItem(this.get(), name);
    }

    default void registerComponent(String name) {
        if (this instanceof IOreDictEntry oreDictEntry) oreDictEntry.registerOreDict();
    }
}

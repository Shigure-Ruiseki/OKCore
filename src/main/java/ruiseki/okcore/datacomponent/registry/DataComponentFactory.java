package ruiseki.okcore.datacomponent.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.datacomponent.core.DataComponentType;

public interface DataComponentFactory<TValue> {

    @Nullable
    DataComponentType<TValue> getComponent(ItemStack stack, Item item, int meta);
}

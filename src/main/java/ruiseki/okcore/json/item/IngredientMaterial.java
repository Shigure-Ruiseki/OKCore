package ruiseki.okcore.json.item;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;

import ruiseki.okcore.json.AbstractJsonMaterial;
import ruiseki.okcore.json.IJsonNetwork;

public abstract class IngredientMaterial extends AbstractJsonMaterial implements Predicate<ItemStack>, IJsonNetwork {
}

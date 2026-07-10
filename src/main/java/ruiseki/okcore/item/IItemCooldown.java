package ruiseki.okcore.item;

import net.minecraft.item.ItemStack;

public interface IItemCooldown {

    UseCooldown getUseCooldown(ItemStack stack);
}

package ruiseki.okcore.item;

import net.minecraft.item.ItemStack;

import ruiseki.okcore.datacomponent.component.UseCooldown;

public interface IItemCooldown {

    UseCooldown getUseCooldown(ItemStack stack);
}

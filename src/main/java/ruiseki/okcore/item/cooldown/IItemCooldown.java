package ruiseki.okcore.item.cooldown;

import net.minecraft.item.ItemStack;

import ruiseki.okcore.datacomponent.component.UseCooldown;

public interface IItemCooldown {

    UseCooldown getUseCooldown(ItemStack stack);
}

package ruiseki.okcore.item;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public interface IItemCooldown {

    @NotNull
    UseCooldown getUseCooldown(ItemStack stack);
}

package ruiseki.okcore.mixins.early.itemSharedNBT;

import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ruiseki.okcore.helper.ItemStackHelpers;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServerNBT {

    @Redirect(
        method = "processClickWindow",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;areItemStacksEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean okcore$compare(ItemStack a, ItemStack b) {
        return ItemStackHelpers.areItemStacksEqualUsingNBTShareTag(a, b);
    }
}

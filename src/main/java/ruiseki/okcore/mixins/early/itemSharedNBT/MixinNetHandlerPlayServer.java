package ruiseki.okcore.mixins.early.itemSharedNBT;

import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.item.IItemSharedTag;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer {

    @WrapOperation(
        method = "processClickWindow",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;areItemStacksEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean okcore$compare(ItemStack a, ItemStack b, Operation<Boolean> original) {
        if (!this.okcore$isSharedTagItem(a) && !this.okcore$isSharedTagItem(b)) {
            return original.call(a, b);
        }

        return ItemHelpers.areItemStacksEqualUsingNBTShareTag(a, b);
    }

    @Unique
    private boolean okcore$isSharedTagItem(ItemStack stack) {
        return stack != null && stack.getItem() instanceof IItemSharedTag;
    }
}

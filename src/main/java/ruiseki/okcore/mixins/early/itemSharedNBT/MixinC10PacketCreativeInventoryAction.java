package ruiseki.okcore.mixins.early.itemSharedNBT;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import ruiseki.okcore.helper.PacketHelpers;
import ruiseki.okcore.item.IItemSharedTag;

@Mixin(C10PacketCreativeInventoryAction.class)
public abstract class MixinC10PacketCreativeInventoryAction {

    @WrapOperation(
        method = "writePacketData",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/PacketBuffer;writeItemStackToBuffer(Lnet/minecraft/item/ItemStack;)V"))
    private void okcore$writeItemStack(PacketBuffer buffer, ItemStack stack, Operation<Void> original)
        throws IOException {
        if (stack == null || !(stack.getItem() instanceof IItemSharedTag)) {
            original.call(buffer, stack);
            return;
        }

        PacketHelpers.writeItemStackFromClientToServer(buffer, stack);
    }
}

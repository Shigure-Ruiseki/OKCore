package ruiseki.okcore.mixins.early.itemSharedNBT;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.okcore.item.IItemSharedTag;

@Mixin(PacketBuffer.class)
public class MixinPacketBufferNBT {

    @Redirect(
        method = "writeItemStackToBuffer",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/item/ItemStack;stackTagCompound:Lnet/minecraft/nbt/NBTTagCompound;",
            opcode = Opcodes.GETFIELD))
    private NBTTagCompound okcore$getNBTShareTag(@NotNull ItemStack stack) {
        return stack.getItem() instanceof IItemSharedTag sharedTag ? sharedTag.getNBTShareTag(stack)
            : stack.stackTagCompound;
    }

    @Inject(method = "readItemStackFromBuffer", at = @At("RETURN"))
    private void okcore$postRead(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        if (stack == null) {
            return;
        }

        if (stack.getItem() instanceof IItemSharedTag sharedTag && stack.stackTagCompound != null) {
            sharedTag.readNBTShareTag(stack, stack.stackTagCompound);
        }
    }
}

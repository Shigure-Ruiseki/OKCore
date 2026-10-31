
package ruiseki.okcore.mixins.early.itemSharedNBT;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import ruiseki.okcore.helper.PacketHelpers;
import ruiseki.okcore.item.IItemSharedTag;

@Mixin(PacketBuffer.class)
public abstract class MixinPacketBuffer {

    @WrapOperation(
        method = "writeItemStackToBuffer",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/item/ItemStack;stackTagCompound:Lnet/minecraft/nbt/NBTTagCompound;",
            opcode = Opcodes.GETFIELD))
    private NBTTagCompound okcore$getNBTShareTag(ItemStack stack, Operation<NBTTagCompound> original) {
        if (!(stack.getItem() instanceof IItemSharedTag sharedTag)
            || PacketHelpers.isWritingClientToServerItemStack()) {
            return original.call(stack);
        }

        return sharedTag.getNBTShareTag(stack);
    }

    @Inject(method = "readItemStackFromBuffer", at = @At("RETURN"))
    private void okcore$postRead(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        if (stack != null && stack.getItem() instanceof IItemSharedTag sharedTag) {
            sharedTag.readNBTShareTag(stack, stack.getTagCompound());
        }
    }
}

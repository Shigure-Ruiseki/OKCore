package ruiseki.okcore.mixins.early.itemSharedNBT;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ruiseki.okcore.helper.PacketHelpers;

@Mixin(C10PacketCreativeInventoryAction.class)
public class MixinC10PacketCreativeInventoryActionNBT {

    @Redirect(
        method = "writePacketData",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/PacketBuffer;writeItemStackToBuffer(Lnet/minecraft/item/ItemStack;)V"))
    private void okcore$writeItemStack(PacketBuffer buffer, ItemStack stack) throws IOException {
        PacketHelpers.writeItemStackFromClientToServer(buffer, stack);
    }
}

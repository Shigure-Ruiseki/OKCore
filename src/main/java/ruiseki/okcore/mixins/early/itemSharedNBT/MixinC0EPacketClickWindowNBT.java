package ruiseki.okcore.mixins.early.itemSharedNBT;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C0EPacketClickWindow;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ruiseki.okcore.helper.PacketHelpers;

@Mixin(C0EPacketClickWindow.class)
public class MixinC0EPacketClickWindowNBT {

    @Redirect(
        method = "writePacketData",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/PacketBuffer;writeItemStackToBuffer(Lnet/minecraft/item/ItemStack;)V"))
    private void okcore$writeItemStack(PacketBuffer buffer, ItemStack stack) throws IOException {
        PacketHelpers.writeItemStackFromClientToServer(buffer, stack);
    }
}

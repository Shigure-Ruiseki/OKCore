package ruiseki.okcore.mixins.early.event.gui;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import ruiseki.okcore.event.gui.SlotClickEvent;
import ruiseki.okcore.inventory.ClickType;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {

    @WrapOperation(
        method = "windowClick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/Container;slotClick(IIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack okcore$wrapSlotClick(Container container, int slotId, int mouseButton, int mode,
        EntityPlayer player, Operation<ItemStack> original) {
        ClickType clickType = ClickType.fromNumber(mode);

        SlotClickEvent.Pre pre = new SlotClickEvent.Pre(container, player, slotId, mouseButton, clickType);

        MinecraftForge.EVENT_BUS.post(pre);

        ItemStack result = pre.isCanceled() ? pre.getResultStack()
            : original.call(container, slotId, mouseButton, mode, player);

        MinecraftForge.EVENT_BUS
            .post(new SlotClickEvent.Post(container, player, slotId, mouseButton, clickType, result));
        return result;
    }
}


package ruiseki.okcore.mixins.early.event.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ruiseki.okcore.event.gui.GuiContainerEvent;
import ruiseki.okcore.event.gui.SlotRenderEvent;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer {

    @Inject(
        method = "drawScreen(IIF)V",
        at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
            ordinal = 3,
            shift = At.Shift.AFTER,
            remap = false))
    private void okcore$onDrawForeground(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS
            .post(new GuiContainerEvent.DrawForeground((GuiContainer) (Object) this, mouseX, mouseY));
    }

    @Inject(method = "func_146977_a", at = @At("HEAD"), cancellable = true)
    private void okcore$renderPre(Slot slot, CallbackInfo ci) {
        SlotRenderEvent.ISlotRenderPosition renderPos = (SlotRenderEvent.ISlotRenderPosition) slot;
        if (MinecraftForge.EVENT_BUS.post(new SlotRenderEvent.Pre(slot, renderPos.getSlotX(), renderPos.getSlotY()))) {
            ci.cancel();
        }
    }

    @Inject(method = "func_146977_a", at = @At("TAIL"))
    private void okcore$renderPost(Slot slot, CallbackInfo ci) {
        SlotRenderEvent.ISlotRenderPosition renderPos = (SlotRenderEvent.ISlotRenderPosition) slot;
        MinecraftForge.EVENT_BUS.post(new SlotRenderEvent.Post(slot, renderPos.getSlotX(), renderPos.getSlotY()));
    }
}

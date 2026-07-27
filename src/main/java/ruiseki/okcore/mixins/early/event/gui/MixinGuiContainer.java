package ruiseki.okcore.mixins.early.event.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ruiseki.okcore.event.gui.GuiContainerEvent;

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
    private void onDrawForeground(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GuiContainerEvent.DrawForeground event = new GuiContainerEvent.DrawForeground(
            (GuiContainer) (Object) this,
            mouseX,
            mouseY);
        MinecraftForge.EVENT_BUS.post(event);
    }
}

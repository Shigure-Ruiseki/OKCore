package ruiseki.okcore.mixins.early.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ruiseki.okcore.client.gui.ISlotBackground;
import ruiseki.okcore.client.gui.component.IWidgetRenderable;
import ruiseki.okcore.client.gui.container.GuiContainerExtended;
import ruiseki.okcore.client.renderer.GlStateManager;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer {

    @Inject(
        method = "drawScreen(IIF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiScreen;drawScreen(IIF)V",
            shift = At.Shift.AFTER))
    private void renderExtendedBackgroundWidgets(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        GuiContainer container = (GuiContainer) (Object) this;
        if (container instanceof GuiContainerExtended<?>extendedGui) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_DEPTH_TEST);

            for (IWidgetRenderable renderable : extendedGui.renderables) {
                renderable.drawScreen(mouseX, mouseY, partialTicks);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    @Inject(
        method = "func_146977_a(Lnet/minecraft/inventory/Slot;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/Slot;getBackgroundIconIndex()Lnet/minecraft/util/IIcon;"),
        cancellable = true)
    private void renderCustomResourceLocationBackground(Slot slotIn, CallbackInfo ci) {
        GuiContainer container = (GuiContainer) (Object) this;
        ItemStack itemstack = slotIn.getStack();

        if (itemstack == null && slotIn instanceof ISlotBackground customSlot) {
            ResourceLocation customTexture = customSlot.getBackgroundTexture();

            if (customTexture != null) {
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                Minecraft.getMinecraft()
                    .getTextureManager()
                    .bindTexture(customTexture);

                GuiContainer.func_152125_a(
                    slotIn.xDisplayPosition,
                    slotIn.yDisplayPosition,
                    0.0F,
                    0.0F,
                    16,
                    16,
                    16,
                    16,
                    16.0F,
                    16.0F);

                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_LIGHTING);

                container.zLevel = 0.0F;
                GuiScreen.itemRender.zLevel = 0.0F;

                ci.cancel();
            }
        }
    }
}

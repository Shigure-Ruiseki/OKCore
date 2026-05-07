package ruiseki.okcore.mixins.early.cooldown;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;

import ruiseki.okcore.helper.EntityHelpers;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem {

    @Inject(
        method = "renderItemOverlayIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
        at = @At("RETURN"))
    private void okcore$injectCooldownOverlay(FontRenderer fontRenderer, TextureManager textureManager, ItemStack stack,
        int x, int y, String text, CallbackInfo ci) {
        if (stack == null || stack.getItem() == null) return;

        EntityPlayerSP entityplayersp = Minecraft.getMinecraft().thePlayer;
        float f3 = entityplayersp == null ? 0.0F
            : EntityHelpers.getCooldownTracker(entityplayersp)
                .getCooldown(stack.getItem(), Minecraft.getMinecraft().timer.renderPartialTicks);

        if (f3 > 0.0F) {

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            Tessellator tessellator = TessellatorManager.get();

            int renderHeight = (int) Math.ceil(16.0F * f3);
            int yOffset = (int) Math.floor(16.0F * (1.0F - f3));

            tessellator.startDrawingQuads();

            tessellator.setColorRGBA_I(0xFFFFFF, 127);

            tessellator.addVertex((x), (y + yOffset + renderHeight), 0.0D);
            tessellator.addVertex((x + 16), (y + yOffset + renderHeight), 0.0D);
            tessellator.addVertex((x + 16), (y + yOffset), 0.0D);
            tessellator.addVertex((x), (y + yOffset), 0.0D);

            tessellator.draw();

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}

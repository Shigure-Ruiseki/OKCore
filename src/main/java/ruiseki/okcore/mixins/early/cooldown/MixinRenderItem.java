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

import ruiseki.okcore.helper.CooldownHelpers;
import ruiseki.okcore.helper.ItemHelpers;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem {

    @Inject(
        method = "renderItemOverlayIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
        at = @At("RETURN"))
    private void okcore$injectCooldownOverlay(FontRenderer fontRenderer, TextureManager textureManager, ItemStack stack,
        int x, int y, String text, CallbackInfo ci) {
        if (ItemHelpers.isEmpty(stack)) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) return;

        float cooldown = CooldownHelpers.getCooldownPercent(stack, player, mc.timer.renderPartialTicks);
        if (cooldown <= 0.0F) return;

        int height = (int) Math.ceil(16.0F * cooldown);
        int yOffset = 16 - height;

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = TessellatorManager.get();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(0xFFFFFF, 127);

        tessellator.addVertex(x, y + 16, 0.0D);
        tessellator.addVertex(x + 16, y + 16, 0.0D);
        tessellator.addVertex(x + 16, y + yOffset, 0.0D);
        tessellator.addVertex(x, y + yOffset, 0.0D);

        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}

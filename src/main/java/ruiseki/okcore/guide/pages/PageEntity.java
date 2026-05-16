package ruiseki.okcore.guide.pages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.Page;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;

import java.util.function.BiFunction;

public class PageEntity extends Page {

    protected String entityName;
    protected String customTitle;

    protected BiFunction<World, String, ? extends EntityLivingBase> supplier;
    protected EntityLivingBase entity;

    public PageEntity(String entityName) {
        this(entityName, (world, name) -> {
            try {
                return (EntityLivingBase) EntityList.createEntityByName(name, world);
            } catch (Exception e) {
                OKCore.okLog(Level.ERROR, e.getMessage());
                return null;
            }
        }, null);
    }

    public PageEntity(String entityName, BiFunction<World, String, ? extends EntityLivingBase> supplier, String customTitle) {
        this.entityName = entityName;
        this.supplier = supplier;
        this.customTitle = customTitle;
    }

    public PageEntity(String entityName, BiFunction<World, String, ? extends EntityLivingBase> supplier) {
        this(entityName, supplier, null);
    }

    @Override
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int pageLeft, int pageTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        if (entity == null && guiBase.player != null && guiBase.player.worldObj != null) {
            entity = supplier.apply(guiBase.player.worldObj, entityName);
        }

        if (entity != null) {
            int x = guiBase.pageXCenter();
            int y = guiBase.pageYCenter() + 30;
            int scale = 40;

            // Render Entity
            drawEntity(x, y, scale, (float) x - mouseX, (float) y - 50 - mouseY, entity);
        }
    }

    public static void drawEntity(int x, int y, int scale, float mouseX, float mouseY, EntityLivingBase entity) {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, 50.0F);
        GL11.glScalef((float) (-scale), (float) scale, (float) scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);

        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f5 = entity.prevRotationYawHead;
        float f6 = entity.rotationYawHead;

        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);

        entity.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
        entity.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
        entity.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;

        GL11.glTranslatef(0.0F, entity.yOffset, 0.0F);
        net.minecraft.client.renderer.entity.RenderManager.instance
            .renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);

        entity.renderYawOffset = f2;
        entity.rotationYaw = f3;
        entity.rotationPitch = f4;
        entity.prevRotationYawHead = f5;
        entity.rotationYawHead = f6;

        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}

package ruiseki.okcore.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.Reference;

/**
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class ParticleBlur extends EntityFX {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        Reference.MOD_ID,
        Reference.TEXTURE_PATH_PARTICLES + "particle_blur.png");
    private static final int MAX_VIEW_DISTANCE = 30;

    private int scaleLife;
    private float originalScale;

    public ParticleBlur(World world, double x, double y, double z, float scale, double motionX, double motionY,
        double motionZ, float red, float green, float blue, float ageMultiplier) {
        super(world, x, y, z, 0, 0, 0);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

        this.particleRed = red;
        this.particleGreen = green;
        this.particleBlue = blue;
        this.particleGravity = 0;

        this.particleScale *= scale;
        this.particleMaxAge = (int) ((rand.nextFloat() * 0.33F + 0.66F) * ageMultiplier);
        this.setSize(0.01F, 0.01F);

        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;

        this.scaleLife = (int) (particleMaxAge / 2.5);
        this.originalScale = this.particleScale;

        validateDistance();
    }

    private void validateDistance() {
        EntityLivingBase renderentity = FMLClientHandler.instance()
            .getClient().thePlayer;
        int visibleDistance = MAX_VIEW_DISTANCE;

        if (!FMLClientHandler.instance()
            .getClient().gameSettings.fancyGraphics) {
            visibleDistance = visibleDistance / 2;
        }

        if (renderentity == null || renderentity.getDistance(posX, posY, posZ) > visibleDistance) {
            this.particleMaxAge = 0;
        }
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTicks, float rotationX, float rotationXZ,
        float rotationZ, float rotationYZ, float rotationXY) {
        float agescale = (float) particleAge / (float) scaleLife;
        if (agescale > 1F) {
            agescale = 2 - agescale;
        }

        particleScale = originalScale * agescale;

        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);

        float f10 = 0.5F * particleScale;
        float f11 = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
        float f12 = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
        float f13 = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

        tessellator.startDrawingQuads();
        tessellator.setBrightness(getBrightnessForRender(partialTicks));
        tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, 0.9F);

        tessellator.addVertexWithUV(
            f11 - rotationX * f10 - rotationYZ * f10,
            f12 - rotationXZ * f10,
            f13 - rotationZ * f10 - rotationXY * f10,
            0,
            1);
        tessellator.addVertexWithUV(
            f11 - rotationX * f10 + rotationYZ * f10,
            f12 + rotationXZ * f10,
            f13 - rotationZ * f10 + rotationXY * f10,
            1,
            1);
        tessellator.addVertexWithUV(
            f11 + rotationX * f10 + rotationYZ * f10,
            f12 + rotationXZ * f10,
            f13 + rotationZ * f10 + rotationXY * f10,
            1,
            0);
        tessellator.addVertexWithUV(
            f11 + rotationX * f10 - rotationYZ * f10,
            f12 - rotationXZ * f10,
            f13 + rotationZ * f10 - rotationXY * f10,
            0,
            0);

        tessellator.draw();

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (particleAge++ >= particleMaxAge) {
            setDead();
        }

        motionY -= 0.04D * particleGravity;
        posX += motionX;
        posY += motionY;
        posZ += motionZ;

        motionX *= 0.98000001907348633D;
        motionY *= 0.98000001907348633D;
        motionZ *= 0.98000001907348633D;
    }

    /**
     * Set the gravity for this particle.
     *
     * @param particleGravity The new gravity
     */
    public void setGravity(float particleGravity) {
        this.particleGravity = particleGravity;
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}

package ruiseki.okcore.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Particle that appears underneath blocks for simulating drops.
 * Ported for Minecraft 1.7.10
 */
@SideOnly(Side.CLIENT)
public class ExtendedParticleDrop extends EntityAuraFX {

    /**
     * The height of the current bob
     */
    private int bobTimer;

    /**
     * Make a new instance.
     * 
     * @param world         The world.
     * @param x             X coordinate.
     * @param y             Y coordinate.
     * @param z             Z coordinate.
     * @param particleRed   Red color.
     * @param particleGreen Green color.
     * @param particleBlue  Blue color.
     */
    public ExtendedParticleDrop(World world, double x, double y, double z, float particleRed, float particleGreen,
        float particleBlue) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.motionX = this.motionY = this.motionZ = 0.0D;

        this.particleRed = particleRed;
        this.particleGreen = particleGreen;
        this.particleBlue = particleBlue;

        this.setParticleTextureIndex(113);
        this.setSize(0.01F, 0.01F);
        this.particleGravity = 0.06F;
        this.bobTimer = 40;
        this.particleMaxAge = (int) (64.0D / (Math.random() * 0.8D + 0.2D));
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.motionY -= (double) this.particleGravity;

        if (this.bobTimer-- > 0) {
            this.motionX *= 0.02D;
            this.motionY *= 0.02D;
            this.motionZ *= 0.02D;
            this.setParticleTextureIndex(113);
        } else {
            this.setParticleTextureIndex(112);
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.particleMaxAge-- <= 0) {
            this.setDead();
        }

        if (this.onGround) {
            this.setParticleTextureIndex(114);

            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }

        int blockX = MathHelper.floor_double(this.posX);
        int blockY = MathHelper.floor_double(this.posY);
        int blockZ = MathHelper.floor_double(this.posZ);

        Material material = this.worldObj.getBlock(blockX, blockY, blockZ)
            .getMaterial();

        if (material.isLiquid() || material.isSolid()) {
            float h = 1.0F;
            Block block = this.worldObj.getBlock(blockX, blockY, blockZ);

            if (block instanceof BlockLiquid) {
                int metadata = this.worldObj.getBlockMetadata(blockX, blockY, blockZ);
                h = BlockLiquid.getLiquidHeightPercent(metadata);
            }

            double d0 = (double) ((float) (blockY + 1) - h);

            if (this.posY < d0) {
                this.setDead();
            }
        }
    }
}

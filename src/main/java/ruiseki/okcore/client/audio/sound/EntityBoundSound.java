package ruiseki.okcore.client.audio.sound;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import org.joml.Vector3d;

import ruiseki.okcore.helper.VectorHelpers;

public class EntityBoundSound extends SoundExtended implements IEntitySound {

    private final Entity entity;

    public EntityBoundSound(ResourceLocation location, float volume, float pitch, Entity entity) {
        super(location, volume, pitch, (float) entity.posX, (float) entity.posY, (float) entity.posZ);
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public boolean canPlaySound() {
        return !this.entity.isDead;
    }

    @Override
    public void update() {
        if (!this.canPlaySound()) {
            this.stop();
        } else {
            Vec3 lookAngle = entity.getLookVec();
            this.xPosF = (float) (this.entity.posX + lookAngle.xCoord);
            this.yPosF = (float) (this.entity.posY + lookAngle.yCoord);
            this.zPosF = (float) (this.entity.posZ + lookAngle.zCoord);
        }
    }

    @Override
    public Vector3d getPosition() {
        return VectorHelpers.createVector3d(this.entity);
    }
}

package ruiseki.okcore.client.audio.sound;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class PlayerBoundSound extends EntityBoundSound {

    private final EntityPlayer player;

    public PlayerBoundSound(ResourceLocation location, float volume, float pitch, EntityPlayer player) {
        super(location, volume, pitch, player);
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    @Override
    public void update() {
        if (!this.canPlaySound()) {
            this.stop();
        } else {
            Vec3 lookAngle = player.getLookVec();
            this.xPosF = (float) (this.player.posX + lookAngle.xCoord);
            this.yPosF = (float) (this.player.posY + this.player.getEyeHeight() + lookAngle.yCoord);
            this.zPosF = (float) (this.player.posZ + lookAngle.zCoord);
        }
    }
}

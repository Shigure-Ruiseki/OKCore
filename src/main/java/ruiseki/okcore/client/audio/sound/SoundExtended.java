
package ruiseki.okcore.client.audio.sound;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.datastructure.BlockPos;

public class SoundExtended extends MovingSound {

    protected final ResourceLocation location;

    protected SoundExtended(ResourceLocation location, float volume, float pitch, float x, float y, float z) {
        super(location);
        this.location = location;
        this.volume = volume;
        this.field_147663_c = pitch;
        this.xPosF = x;
        this.yPosF = y;
        this.zPosF = z;
    }

    protected SoundExtended(ResourceLocation location, BlockPos pos) {
        this(location, 4, 1, pos.getX(), pos.getY(), pos.getZ());
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public void stop() {
        this.donePlaying = true;
        this.repeat = false;
    }

    @Override
    public void update() {

    }
}

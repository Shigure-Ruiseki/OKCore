
package ruiseki.okcore.client.audio.sound;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.ResourceLocation;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class SoundExtended extends MovingSound {

    protected final ResourceLocation location;

    public SoundExtended(ResourceLocation location, float volume, float pitch, float x, float y, float z) {
        super(location);
        this.location = location;
        this.volume = volume;
        this.field_147663_c = pitch;
        this.xPosF = x;
        this.yPosF = y;
        this.zPosF = z;
    }

    public SoundExtended(ResourceLocation location, Vector3i pos) {
        this(location, 4, 1, pos.x, pos.y, pos.z);
    }

    public SoundExtended(ResourceLocation location, Vector3f pos) {
        this(location, 4, 1, pos.x, pos.y, pos.z);
    }

    public SoundExtended(ResourceLocation location, Vector3d pos) {
        this(location, 4, 1, (float) pos.x, (float) pos.y, (float) pos.z);
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

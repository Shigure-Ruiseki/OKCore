package ruiseki.okcore.event.interaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public interface IEntityInteractAt {

    default InteractionResult interactAt(EntityPlayer player, Vec3 vec3) {
        return InteractionResult.PASS;
    }
}

package ruiseki.okcore.event.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;

public class LivingClimbEvent extends LivingEvent {

    private boolean canClimb;

    public LivingClimbEvent(EntityLivingBase entity, boolean canClimb) {
        super(entity);
        this.canClimb = canClimb;
    }

    public void setCanClimb(boolean canClimb) {
        this.canClimb = canClimb;
    }

    public boolean isCanClimb() {
        return canClimb;
    }
}

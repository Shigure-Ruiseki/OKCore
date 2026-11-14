package ruiseki.okcore.event.entity;

import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;

public class GhastTargetEvent extends LivingEvent {

    private final double maxDistance;
    private EntityPlayer target;

    public GhastTargetEvent(EntityGhast ghast, double maxDistance, EntityPlayer target) {
        super(ghast);
        this.maxDistance = maxDistance;
        this.target = target;
    }

    public EntityGhast getGhast() {
        return (EntityGhast) entityLiving;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public EntityPlayer getTarget() {
        return target;
    }

    public void setTarget(EntityPlayer target) {
        this.target = target;
    }
}

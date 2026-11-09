package ruiseki.okcore.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraftforge.event.entity.living.LivingEvent;

public class GhastTargetEvent extends LivingEvent {

    private final double maxDistance;
    private Entity target;

    public GhastTargetEvent(EntityGhast ghast, double maxDistance, Entity target) {
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

    public Entity getTarget() {
        return target;
    }
}

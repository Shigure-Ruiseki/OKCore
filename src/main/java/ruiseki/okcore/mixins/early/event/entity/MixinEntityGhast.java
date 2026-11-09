package ruiseki.okcore.mixins.early.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ruiseki.okcore.event.entity.GhastTargetEvent;

@Mixin(EntityGhast.class)
public abstract class MixinEntityGhast {

    @Shadow
    private Entity targetedEntity;

    @Inject(
        method = "updateEntityActionState",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getClosestVulnerablePlayerToEntity(Lnet/minecraft/entity/Entity;D)Lnet/minecraft/entity/player/EntityPlayer;",
            shift = At.Shift.AFTER))
    private void okcore$onFindTarget(CallbackInfo ci) {
        GhastTargetEvent event = new GhastTargetEvent((EntityGhast) (Object) this, 100.0D, targetedEntity);
        MinecraftForge.EVENT_BUS.post(event);
        targetedEntity = event.getTarget();
    }
}

package ruiseki.okcore.mixins.early.event.entity;

import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import ruiseki.okcore.event.entity.GhastTargetEvent;

@Mixin(EntityGhast.class)
public abstract class MixinEntityGhast {

    @ModifyExpressionValue(
        method = "updateEntityActionState",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getClosestVulnerablePlayerToEntity(Lnet/minecraft/entity/Entity;D)Lnet/minecraft/entity/player/EntityPlayer;"))
    private EntityPlayer okcore$onFindTarget(EntityPlayer original) {
        GhastTargetEvent event = new GhastTargetEvent((EntityGhast) (Object) this, 100.0D, original);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getTarget();
    }
}

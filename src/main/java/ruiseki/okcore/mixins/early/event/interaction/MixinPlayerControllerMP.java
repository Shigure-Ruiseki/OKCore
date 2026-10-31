
package ruiseki.okcore.mixins.early.event.interaction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.okcore.event.interaction.InteractionResult;
import ruiseki.okcore.helper.PlayerHelpers;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {

    /**
     * Handles position-specific entity interactions before
     * the standard entity interaction.
     */
    @Inject(method = "interactWithEntitySendPacket", at = @At("HEAD"), cancellable = true)
    private void okcore$interactEntityAt(EntityPlayer player, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getMinecraft();
        MovingObjectPosition hit = mc.objectMouseOver;
        if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY
            || hit.entityHit != entity
            || hit.hitVec == null) {
            return;
        }

        InteractionResult result = PlayerHelpers.interactAt(player, entity, hit);
        if (result == null || !result.consumesAction()) return;
        if (result.shouldSwing()) player.swingItem();
        cir.setReturnValue(true);
    }
}

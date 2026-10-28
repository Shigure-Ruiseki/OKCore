
package ruiseki.okcore.mixins.early.event.interaction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ruiseki.okcore.event.interaction.PlayerInteractEvent;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Shadow
    public EntityClientPlayerMP thePlayer;

    @Shadow
    public MovingObjectPosition objectMouseOver;

    /**
     * Fires when the player attacks empty space.
     */
    @Inject(method = "func_147116_af", at = @At("HEAD"))
    private void okcore$leftClickEmpty(CallbackInfo ci) {
        if (thePlayer == null) {
            return;
        }

        if (objectMouseOver == null || objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {

            MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.LeftClickEmpty(thePlayer));
        }
    }
}

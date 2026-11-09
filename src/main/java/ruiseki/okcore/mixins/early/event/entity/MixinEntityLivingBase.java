package ruiseki.okcore.mixins.early.event.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.okcore.event.entity.LivingClimbEvent;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {

    @Inject(method = "isOnLadder", at = @At("RETURN"), cancellable = true)
    private void okcore$checkClimb(CallbackInfoReturnable<Boolean> cir) {
        LivingClimbEvent event = new LivingClimbEvent(this.okcore$getThis(), cir.getReturnValue());
        MinecraftForge.EVENT_BUS.post(event);
        cir.setReturnValue(event.isCanClimb());
    }

    @Unique
    private EntityLivingBase okcore$getThis() {
        return (EntityLivingBase) (Object) this;
    }

}

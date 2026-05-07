package ruiseki.okcore.mixins.early.cooldown;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ruiseki.okcore.entity.cooldown.CooldownTracker;
import ruiseki.okcore.entity.cooldown.CooldownTrackerServer;
import ruiseki.okcore.entity.cooldown.ICooldownHandler;

@Mixin(EntityPlayer.class)
@Implements(@Interface(iface = ICooldownHandler.class, prefix = "okCore$"))
public abstract class MixinEntityPlayer {

    @Unique
    private CooldownTracker cooldownTracker = this.oKCore$createCooldownTracker();

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onPlayerUpdate(CallbackInfo ci) {
        if (this.cooldownTracker != null) {
            this.cooldownTracker.tick();
        }
    }

    @Unique
    private CooldownTracker oKCore$createCooldownTracker() {
        if ((Object) this instanceof EntityPlayerMP) {
            return this.cooldownTracker = new CooldownTrackerServer((EntityPlayerMP) (Object) this);
        } else {
            return this.cooldownTracker = new CooldownTracker();
        }
    }

    public CooldownTracker okCore$getCooldownTracker() {
        return this.cooldownTracker;
    }
}

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

import ruiseki.okcore.entity.cooldown.ICooldownHandler;
import ruiseki.okcore.entity.cooldown.ItemCooldowns;
import ruiseki.okcore.entity.cooldown.ItemCooldownsServer;

@Mixin(EntityPlayer.class)
@Implements(@Interface(iface = ICooldownHandler.class, prefix = "okcorecool$"))
public abstract class MixinEntityPlayer {

    @Unique
    private ItemCooldowns okcore$itemCooldowm = this.okcore$createItemCooldowns();

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void okcore$onPlayerUpdate(CallbackInfo ci) {
        if (this.okcore$itemCooldowm != null) {
            this.okcore$itemCooldowm.tick();
        }
    }

    @Unique
    private ItemCooldowns okcore$createItemCooldowns() {
        if ((Object) this instanceof EntityPlayerMP) {
            return this.okcore$itemCooldowm = new ItemCooldownsServer((EntityPlayerMP) (Object) this);
        } else {
            return this.okcore$itemCooldowm = new ItemCooldowns();
        }
    }

    public ItemCooldowns okcorecool$getItemCooldowns() {
        return this.okcore$itemCooldowm;
    }
}

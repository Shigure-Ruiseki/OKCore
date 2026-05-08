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
@Implements(@Interface(iface = ICooldownHandler.class, prefix = "okCore$"))
public abstract class MixinEntityPlayer {

    @Unique
    private ItemCooldowns oKCore$itemCooldowm = this.oKCore$createItemCooldowns();

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onPlayerUpdate(CallbackInfo ci) {
        if (this.oKCore$itemCooldowm != null) {
            this.oKCore$itemCooldowm.tick();
        }
    }

    @Unique
    private ItemCooldowns oKCore$createItemCooldowns() {
        if ((Object) this instanceof EntityPlayerMP) {
            return this.oKCore$itemCooldowm = new ItemCooldownsServer((EntityPlayerMP) (Object) this);
        } else {
            return this.oKCore$itemCooldowm = new ItemCooldowns();
        }
    }

    public ItemCooldowns okCore$getItemCooldowns() {
        return this.oKCore$itemCooldowm;
    }
}

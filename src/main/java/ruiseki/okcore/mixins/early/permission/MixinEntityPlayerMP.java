package ruiseki.okcore.mixins.early.permission;

import net.minecraft.entity.player.EntityPlayerMP;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.entity.permission.IPlayerPermission;
import ruiseki.okcore.helper.PlayerHelpers;

@Mixin(EntityPlayerMP.class)
@Implements({ @Interface(iface = IPlayerPermission.class, prefix = "okcoreper$") })
public abstract class MixinEntityPlayerMP {

    public int okcoreper$getPermissionLevel() {
        return PlayerHelpers.getPermissionLevel(
            this.okcore$getThis()
                .getGameProfile());
    }

    public void okcoreper$setPermissionLevel(int level) {
        // MOOP
    }

    public boolean okcoreper$hasPermissions(int level) {
        return this.okcoreper$getPermissionLevel() >= level;
    }

    @Unique
    public EntityPlayerMP okcore$getThis() {
        return (EntityPlayerMP) (Object) this;
    }
}

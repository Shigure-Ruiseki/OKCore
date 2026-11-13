package ruiseki.okcore.mixins.early.permission;

import net.minecraft.client.entity.EntityPlayerSP;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.entity.permission.IPlayerPermission;

@Mixin(EntityPlayerSP.class)
@Implements({ @Interface(iface = IPlayerPermission.class, prefix = "okcoreper$") })
public abstract class MixinEntityPlayerSP {

    @Unique
    private int okcore$permissionLevel;

    public int okcoreper$getPermissionLevel() {
        return okcore$permissionLevel;
    }

    public void okcoreper$setPermissionLevel(int level) {
        this.okcore$permissionLevel = Math.clamp(level, 0, 4);
    }

    public boolean okcoreper$hasPermissions(int level) {
        return this.okcore$permissionLevel >= level;
    }
}

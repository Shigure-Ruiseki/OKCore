package ruiseki.okcore.mixins.early.entity.permission;

import net.minecraft.client.entity.EntityPlayerSP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.entity.permission.IPlayerPermission;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP implements IPlayerPermission {

    @Unique
    private int okcore$permissionLevel;

    @Override
    public int okcore$getPermissionLevel() {
        return okcore$permissionLevel;
    }

    @Override
    public void okcore$setPermissionLevel(int level) {
        this.okcore$permissionLevel = Math.clamp(level, 0, 4);
    }

    @Override
    public boolean okcore$hasPermissions(int level) {
        return this.okcore$permissionLevel >= level;
    }
}

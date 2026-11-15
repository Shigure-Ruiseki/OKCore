package ruiseki.okcore.mixins.early.entity.permission;

import net.minecraft.entity.player.EntityPlayerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.entity.permission.IPlayerPermission;
import ruiseki.okcore.helper.PlayerHelpers;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP implements IPlayerPermission {

    @Override
    public int okcore$getPermissionLevel() {
        return PlayerHelpers.getPermissionLevel(
            this.okcore$getThis()
                .getGameProfile());
    }

    @Override
    public void okcore$setPermissionLevel(int level) {
        // MOOP
    }

    @Override
    public boolean okcore$hasPermissions(int level) {
        return this.okcore$getPermissionLevel() >= level;
    }

    @Unique
    public EntityPlayerMP okcore$getThis() {
        return (EntityPlayerMP) (Object) this;
    }
}

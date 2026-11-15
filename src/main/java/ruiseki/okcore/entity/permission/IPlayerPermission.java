package ruiseki.okcore.entity.permission;

public interface IPlayerPermission {

    int okcore$getPermissionLevel();

    void okcore$setPermissionLevel(int level);

    boolean okcore$hasPermissions(int level);
}

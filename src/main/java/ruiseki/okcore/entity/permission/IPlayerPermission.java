package ruiseki.okcore.entity.permission;

public interface IPlayerPermission {

    int getPermissionLevel();

    void setPermissionLevel(int level);

    boolean hasPermissions(int level);
}

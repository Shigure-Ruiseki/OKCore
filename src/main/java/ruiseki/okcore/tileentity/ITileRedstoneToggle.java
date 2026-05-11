package ruiseki.okcore.tileentity;

public interface ITileRedstoneToggle {

    void toggleNeedsRedstone();

    boolean onlyRunIfPowered();
}

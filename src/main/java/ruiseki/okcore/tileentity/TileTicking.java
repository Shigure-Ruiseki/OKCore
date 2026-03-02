package ruiseki.okcore.tileentity;

import lombok.experimental.Delegate;

public class TileTicking extends TileSideCapability implements TileEntityOK.ITickingTile {

    @Delegate
    protected final ITickingTile tickingTileComponent = new TickingTileComponent(this);

    public TileTicking() {}
}

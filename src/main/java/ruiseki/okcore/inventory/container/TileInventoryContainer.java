package ruiseki.okcore.inventory.container;

import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import ruiseki.okcore.client.gui.GuiType;
import ruiseki.okcore.tileentity.InventoryTileEntity;

/**
 * A container for a tile entity with inventory.
 *
 * @author rubensworks
 *
 * @param <T> The type of tile.
 */
public class TileInventoryContainer<T extends InventoryTileEntity> extends InventoryContainer {

    protected Optional<T> tile;

    /**
     * Make a new TileInventoryContainer.
     *
     * @param inventory The player inventory.
     * @param tile      The TileEntity for this container.
     */
    public TileInventoryContainer(GuiType<?> guiType, InventoryPlayer playerInventory, IInventory inventory,
        Optional<T> tile) {
        super(guiType, playerInventory, inventory);
        this.tile = tile;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return tile.map(t -> t.canInteractWith(entityPlayer))
            .orElse(false);
    }

    /**
     * @return The tile entity.
     */
    public Optional<T> getTile() {
        return tile;
    }
}

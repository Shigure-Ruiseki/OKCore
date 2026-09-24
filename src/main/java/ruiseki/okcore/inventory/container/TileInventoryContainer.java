package ruiseki.okcore.inventory.container;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import org.jetbrains.annotations.NotNull;

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

    @Nullable
    protected final T tile;

    public TileInventoryContainer(GuiType<?> guiType, InventoryPlayer playerInventory, IInventory inventory,
        @Nullable T tile) {
        super(guiType, playerInventory, inventory);
        this.tile = tile;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return getTile().map(t -> t.canInteractWith(entityPlayer))
            .orElse(false);
    }

    /**
     * @return The tile entity wrapped in an Optional.
     */
    @NotNull
    public Optional<T> getTile() {
        return Optional.ofNullable(tile);
    }
}

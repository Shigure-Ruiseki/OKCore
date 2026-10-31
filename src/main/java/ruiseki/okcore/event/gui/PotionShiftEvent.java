
package ruiseki.okcore.event.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraftforge.client.event.GuiScreenEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

/**
 * Fired when an {@link InventoryEffectRenderer} attempts to reposition
 * its GUI to accommodate active potion effects.
 *
 * <p>
 * This event is triggered during
 * {@code InventoryEffectRenderer.updateActivePotionEffects()} when
 * active potion effects would cause the GUI to shift horizontally.
 *
 * <p>
 * The event is {@link Cancelable}. Canceling it prevents the GUI
 * from being repositioned while allowing potion effects to remain active.
 *
 * <h2>Usage</h2>
 *
 * <p>
 * Subscribe to this event through the appropriate Forge event bus
 * to control whether the GUI should be repositioned.
 *
 * @see InventoryEffectRenderer
 * @see GuiScreenEvent
 * @see Cancelable
 */
@Cancelable
public class PotionShiftEvent extends GuiScreenEvent {

    /**
     * Creates a new potion shift event for the specified GUI.
     *
     * @param gui the GUI screen attempting to reposition itself
     */
    public PotionShiftEvent(GuiScreen gui) {
        super(gui);
    }
}

package ruiseki.okcore.event.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraftforge.client.event.GuiScreenEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

/**
 * This event fires in {@link InventoryEffectRenderer#updateActivePotionEffects()}
 * when potion effects are active and the gui wants to move over.
 * Cancel this event to prevent the Gui from being moved.
 */
@Cancelable
public class PotionShiftEvent extends GuiScreenEvent {

    public PotionShiftEvent(GuiScreen gui) {
        super(gui);
    }
}

package ruiseki.okcore.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.client.event.MouseInputEvent;
import ruiseki.okcore.item.IItemToggle;
import ruiseki.okcore.network.packet.PacketItemToggle;

public class GuiItemToggleEvent {

    public static final GuiItemToggleEvent INSTANCE = new GuiItemToggleEvent();

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMouseInput(MouseInputEvent.Pre event) {
        if (!(event.gui instanceof GuiContainer gui)) return;

        int button = Mouse.getEventButton();
        boolean isPressed = Mouse.getEventButtonState();

        if (button != -1 && isPressed) {
            Minecraft mc = Minecraft.getMinecraft();

            final int x = Mouse.getEventX() * gui.width / mc.displayWidth;
            final int y = gui.height - Mouse.getEventY() * gui.height / mc.displayHeight - 1;

            Slot slot = gui.getSlotAtPosition(x, y);

            if (slot != null && slot.getHasStack()) {
                ItemStack stack = slot.getStack();

                if (stack.getItem() instanceof IItemToggle toggle) {
                    boolean shiftOK = !toggle.needsShiftClick(stack) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);

                    if (toggle.canMouseClicked(stack, button) && shiftOK) {
                        OKCore.instance.getPacketHandler()
                            .sendToServer(new PacketItemToggle(slot.slotNumber));

                        event.setCanceled(true);

                        mc.thePlayer.playSound("random.click", 0.3F, 0.5F);
                    }
                }
            }
        }
    }
}

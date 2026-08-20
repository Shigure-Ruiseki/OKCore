package ruiseki.okcore.event.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.event.input.IGuiInputHandle;
import ruiseki.okcore.event.input.MouseInputEvent;
import ruiseki.okcore.item.IItemToggle;
import ruiseki.okcore.network.packet.PacketItemToggle;

public class InputEventHandler {

    public static final InputEventHandler INSTANCE = new InputEventHandler();

    public InputEventHandler() {}

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
                    if (toggle.canMouseClicked(stack, button) && toggle.isModifierKeyDown(stack)) {
                        int sendSlotNumber = slot.slotNumber;
                        if (gui instanceof GuiContainerCreative) {
                            if (slot.inventory instanceof InventoryPlayer) {
                                sendSlotNumber = slot.getSlotIndex();
                            } else {
                                sendSlotNumber = -1;
                            }
                        }
                        OKCore._instance.getPacketHandler()
                            .sendToServer(new PacketItemToggle(sendSlotNumber, button));
                        if (gui instanceof IGuiInputHandle handle) {
                            handle.setMouseHandled(true);
                        }

                        event.setCanceled(true);

                        mc.thePlayer.playSound("random.click", 0.3F, 0.5F);
                    }
                }
            }
        }
    }
}

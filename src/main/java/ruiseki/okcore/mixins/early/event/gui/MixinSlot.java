package ruiseki.okcore.mixins.early.event.gui;

import net.minecraft.inventory.Slot;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.event.gui.SlotRenderEvent;

@Mixin(Slot.class)
@Implements({ @Interface(iface = SlotRenderEvent.ISlotRenderPosition.class, prefix = "okcorecxy$") })
public abstract class MixinSlot {

    @Unique
    private Slot okcore$getThis() {
        return (Slot) (Object) this;
    }

    public int okcorecxy$getSlotX() {
        return this.okcore$getThis().xDisplayPosition;
    }

    public int okcorecxy$getSlotY() {
        return this.okcore$getThis().yDisplayPosition;
    }
}

package ruiseki.okcore.mixins.early.event.gui;

import net.minecraft.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.event.gui.SlotRenderEvent;

@Mixin(Slot.class)
public abstract class MixinSlot implements SlotRenderEvent.ISlotRenderPosition {

    @Unique
    private Slot okcore$getThis() {
        return (Slot) (Object) this;
    }

    @Override
    public int okcore$getSlotX() {
        return this.okcore$getThis().xDisplayPosition;
    }

    @Override
    public int okcore$getSlotY() {
        return this.okcore$getThis().yDisplayPosition;
    }
}

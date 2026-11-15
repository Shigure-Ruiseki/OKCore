package ruiseki.okcore.mixins.early.mui;

import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import ruiseki.okcore.event.gui.SlotRenderEvent;

@Mixin(ItemSlot.class)
public abstract class MixinItemSlot implements SlotRenderEvent.ISlotRenderPosition {

    @Inject(method = "draw", at = @At("HEAD"), remap = false, cancellable = true)
    private void okcore$renderPre(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(
            new SlotRenderEvent.Pre(
                this.okcore$getThis()
                    .getSlot(),
                this.okcore$getSlotX(),
                this.okcore$getSlotY()))) {
            ci.cancel();
        }
    }

    @Inject(method = "draw", at = @At("TAIL"), remap = false)
    private void okcore$renderPos(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(
            new SlotRenderEvent.Post(
                this.okcore$getThis()
                    .getSlot(),
                this.okcore$getSlotX(),
                this.okcore$getSlotY()));
    }

    @Unique
    private ItemSlot okcore$getThis() {
        return (ItemSlot) (Object) this;
    }

    @Override
    public int okcore$getSlotX() {
        return 1;
    }

    @Override
    public int okcore$getSlotY() {
        return 1;
    }
}

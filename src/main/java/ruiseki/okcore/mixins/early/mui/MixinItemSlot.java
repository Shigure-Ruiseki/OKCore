package ruiseki.okcore.mixins.early.mui;

import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.okcore.event.gui.SlotRenderEvent;

@Mixin(ItemSlot.class)
public abstract class MixinItemSlot {

    @Shadow
    public abstract ModularSlot getSlot();

    @Inject(method = "draw", at = @At("HEAD"), remap = false, cancellable = true)
    private void okcore$renderPre(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(
            new SlotRenderEvent.Pre(
                this.okcore$getThis()
                    .getSlot()))) {
            ci.cancel();
        }
    }

    @Inject(method = "draw", at = @At("TAIL"), remap = false)
    private void okcore$renderPos(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(
            new SlotRenderEvent.Post(
                this.okcore$getThis()
                    .getSlot()));
    }

    @Unique
    private ItemSlot okcore$getThis() {
        return (ItemSlot) (Object) this;
    }
}

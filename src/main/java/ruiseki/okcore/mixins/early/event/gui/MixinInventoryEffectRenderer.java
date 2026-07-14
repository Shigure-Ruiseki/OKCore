package ruiseki.okcore.mixins.early.event.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Container;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ruiseki.okcore.event.gui.PotionShiftEvent;

@Mixin(InventoryEffectRenderer.class)
public abstract class MixinInventoryEffectRenderer extends GuiContainer {

    @Shadow
    private boolean field_147045_u;

    public MixinInventoryEffectRenderer(Container container) {
        super(container);
    }

    @Inject(
        method = "initGui",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/InventoryEffectRenderer;guiLeft:I",
            opcode = 181,
            shift = At.Shift.BEFORE),
        cancellable = true)
    private void onPotionShift(CallbackInfo ci) {
        PotionShiftEvent event = new PotionShiftEvent(this);
        boolean isCanceled = MinecraftForge.EVENT_BUS.post(event);

        if (isCanceled) {
            this.guiLeft = (this.width - this.xSize) / 2;
            this.field_147045_u = true;
            ci.cancel();
        }
    }
}

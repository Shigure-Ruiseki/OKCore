package ruiseki.okcore.mixins.early.event.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import ruiseki.okcore.event.gui.BackgroundDrawnEvent;
import ruiseki.okcore.event.input.IGuiInputHandle;
import ruiseki.okcore.event.input.KeyboardInputEvent;
import ruiseki.okcore.event.input.MouseInputEvent;

@Mixin(GuiScreen.class)
@Implements(@Interface(iface = IGuiInputHandle.class, prefix = "okcoregui$"))
public abstract class MixinGuiScreen {

    @Unique
    private boolean okcore$keyHandled;
    @Unique
    private boolean okcore$mouseHandled;

    @WrapOperation(
        method = "handleInput",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleMouseInput()V"))
    private void okcore$wrapMouseInput(GuiScreen instance, Operation<Void> original) {
        this.okcore$mouseHandled = false;
        if (MinecraftForge.EVENT_BUS.post(new MouseInputEvent.Pre(okcore$getThis()))) {
            return;
        }

        original.call(instance);

        if (okcore$getThis().equals(okcore$getThis().mc.currentScreen) && !this.okcoregui$isMouseHandled()) {
            MinecraftForge.EVENT_BUS.post(new MouseInputEvent.Post(okcore$getThis()));
        }
    }

    @WrapOperation(
        method = "handleInput",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleKeyboardInput()V"))
    private void okcore$wrapKeyInput(GuiScreen instance, Operation<Void> original) {
        this.okcore$keyHandled = false;
        if (MinecraftForge.EVENT_BUS.post(new KeyboardInputEvent.Pre(okcore$getThis()))) {
            return;
        }

        original.call(instance);

        // Post Event
        if (okcore$getThis().equals(okcore$getThis().mc.currentScreen) && !this.okcoregui$isKeyHandled()) {
            MinecraftForge.EVENT_BUS.post(new KeyboardInputEvent.Post(okcore$getThis()));
        }
    }

    @Inject(method = "drawDefaultBackground", at = @At("RETURN"))
    private void okcore$drawDefaultBackground(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new BackgroundDrawnEvent(okcore$getThis()));
    }

    public void okcoregui$setMouseHandled(boolean handled) {
        this.okcore$mouseHandled = handled;
    }

    public boolean okcoregui$isMouseHandled() {
        return this.okcore$mouseHandled;
    }

    public void okcoregui$setKeyHandled(boolean handled) {
        this.okcore$keyHandled = handled;
    }

    public boolean okcoregui$isKeyHandled() {
        return this.okcore$keyHandled;
    }

    @Unique
    private GuiScreen okcore$getThis() {
        return (GuiScreen) (Object) this;
    }
}

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
public abstract class MixinGuiScreen implements IGuiInputHandle {

    @Unique
    private boolean okcore$keyHandled;
    @Unique
    private boolean okcore$mouseHandled;

    @WrapOperation(
        method = "handleInput",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleMouseInput()V"))
    private void okcore$wrapMouseInput(GuiScreen instance, Operation<Void> original) {
        this.okcore$mouseHandled = false;

        // 1. Check Pre
        MouseInputEvent.Pre preEvent = new MouseInputEvent.Pre(okcore$getThis());
        if (MinecraftForge.EVENT_BUS.post(preEvent)) {
            return;
        }

        // 2. Check Process
        MouseInputEvent.Process processEvent = new MouseInputEvent.Process(okcore$getThis());
        MinecraftForge.EVENT_BUS.post(processEvent);

        if (processEvent.isCanceled() || this.okcore$mouseHandled) {
            this.okcore$mouseHandled = true;
            return; // Block Vanilla handleMouseInput()
        }

        // 3. Call Vanilla
        original.call(instance);

        // 4. Post Event
        if (okcore$getThis().equals(okcore$getThis().mc.currentScreen) && !this.okcore$mouseHandled) {
            MinecraftForge.EVENT_BUS.post(new MouseInputEvent.Post(okcore$getThis()));
        }
    }

    @WrapOperation(
        method = "handleInput",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleKeyboardInput()V"))
    private void okcore$wrapKeyInput(GuiScreen instance, Operation<Void> original) {
        this.okcore$keyHandled = false;

        // 1. Check Pre
        KeyboardInputEvent.Pre preEvent = new KeyboardInputEvent.Pre(okcore$getThis());
        if (MinecraftForge.EVENT_BUS.post(preEvent)) {
            return;
        }

        // 2. Check Process
        KeyboardInputEvent.Process processEvent = new KeyboardInputEvent.Process(okcore$getThis());
        MinecraftForge.EVENT_BUS.post(processEvent);

        if (processEvent.isCanceled() || this.okcore$keyHandled) {
            this.okcore$keyHandled = true;
            return; // Block Vanilla handleKeyboardInput()
        }

        // 3. Call Vanilla
        original.call(instance);

        // 4. Post Event
        if (okcore$getThis().equals(okcore$getThis().mc.currentScreen) && !this.okcore$keyHandled) {
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

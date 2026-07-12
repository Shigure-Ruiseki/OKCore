package ruiseki.okcore.mixins.early.event.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ruiseki.okcore.event.input.IGuiInputHandle;
import ruiseki.okcore.event.input.KeyboardInputEvent;
import ruiseki.okcore.event.input.MouseInputEvent;

@Mixin(GuiScreen.class)
@Implements(@Interface(iface = IGuiInputHandle.class, prefix = "okcoregui$"))
public abstract class MixinGuiScreen {

    @Shadow
    public Minecraft mc;

    @Unique
    private boolean okcore$keyHandled;
    @Unique
    private boolean okcore$mouseHandled;

    @Inject(
        method = "handleInput",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleMouseInput()V"),
        cancellable = true)
    private void okcore$preMouseInput(CallbackInfo ci) {
        this.okcore$mouseHandled = false;
        if (MinecraftForge.EVENT_BUS.post(new MouseInputEvent.Pre(okcore$getThis()))) {
            ci.cancel();
        }
    }

    @Inject(
        method = "handleInput",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiScreen;handleMouseInput()V",
            shift = At.Shift.AFTER))
    private void okcore$postMouseInput(CallbackInfo ci) {
        if (okcore$getThis().equals(this.mc.currentScreen) && !this.okcoregui$isMouseHandled()) {
            MinecraftForge.EVENT_BUS.post(new MouseInputEvent.Post(okcore$getThis()));
        }
    }

    @Inject(
        method = "handleInput",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;handleKeyboardInput()V"),
        cancellable = true)
    private void okcore$preKeyInput(CallbackInfo ci) {
        this.okcore$keyHandled = false;
        if (MinecraftForge.EVENT_BUS.post(new KeyboardInputEvent.Pre(okcore$getThis()))) {
            ci.cancel();
        }
    }

    @Inject(
        method = "handleInput",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiScreen;handleKeyboardInput()V",
            shift = At.Shift.AFTER))
    private void okcore$postKeyInput(CallbackInfo ci) {
        if (okcore$getThis().equals(this.mc.currentScreen) && !this.okcoregui$isKeyHandled()) {
            MinecraftForge.EVENT_BUS.post(new KeyboardInputEvent.Post(okcore$getThis()));
        }
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

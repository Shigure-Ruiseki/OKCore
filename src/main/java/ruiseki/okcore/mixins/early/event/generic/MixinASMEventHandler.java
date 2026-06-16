package ruiseki.okcore.mixins.early.event.generic;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.ASMEventHandler;
import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.okcore.event.generic.IGenericEvent;

@Mixin(value = ASMEventHandler.class)
public class MixinASMEventHandler {

    @Unique
    public Type okcore$filter;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void okcore$onInit(Object target, Method method, ModContainer owner, CallbackInfo ci) {
        if (method.getParameterTypes().length == 0) return;
        Class<?> eventClass = method.getParameterTypes()[0];
        if (!IGenericEvent.class.isAssignableFrom(eventClass)) return;
        Type type = method.getGenericParameterTypes()[0];
        if (type instanceof ParameterizedType) {
            this.okcore$filter = ((ParameterizedType) type).getActualTypeArguments()[0];
        }
    }

    @Inject(
        method = "invoke(Lcpw/mods/fml/common/eventhandler/Event;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcpw/mods/fml/common/eventhandler/IEventListener;invoke(Lcpw/mods/fml/common/eventhandler/Event;)V"),
        cancellable = true,
        remap = false)
    private void okcore$injectFilter(Event event, CallbackInfo ci) {
        if (this.okcore$filter != null) {
            if (!(event instanceof IGenericEvent<?>)
                || this.okcore$filter != ((IGenericEvent<?>) event).getGenericType()) {
                ci.cancel();
            }
        }
    }
}

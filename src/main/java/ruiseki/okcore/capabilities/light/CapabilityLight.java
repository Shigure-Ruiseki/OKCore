package ruiseki.okcore.capabilities.light;

import ruiseki.okcore.block.IDynamicLight;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.init.IInitListener;

public class CapabilityLight implements IInitListener {

    @CapabilityInject(IDynamicLight.class)
    public static Capability<IDynamicLight> DYNAMIC_LIGHT_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IDynamicLight.class, DynamicLightDefault::new);
    }

    @Override
    public void onInit(IInitListener.Step initStep) {
        if (initStep == IInitListener.Step.PREINIT) {
            register();
        }
    }
}

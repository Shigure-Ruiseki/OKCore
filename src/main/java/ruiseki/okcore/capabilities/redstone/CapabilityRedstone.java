package ruiseki.okcore.capabilities.redstone;

import ruiseki.okcore.block.IDynamicRedstone;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.init.IInitListener;

public class CapabilityRedstone implements IInitListener {

    @CapabilityInject(IDynamicRedstone.class)
    public static Capability<IDynamicRedstone> DYNAMIC_REDSTONE_CAPABILITY = null;

    @Override
    public void onInit(IInitListener.Step initStep) {
        if (initStep != IInitListener.Step.PREINIT) return;
        CapabilityManager.INSTANCE.register(IDynamicRedstone.class);
    }
}

package ruiseki.okcore.core.item.gui;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

public class CapabilityTestGuiWrapperConfig extends CapabilityConfig<TestGuiWrapper> {

    public static CapabilityTestGuiWrapperConfig _instance;

    @CapabilityInject(TestGuiWrapper.class)
    public static Capability<TestGuiWrapper> CAPABILITY = null;

    public CapabilityTestGuiWrapperConfig() {
        super(OKCore._instance, true, "test_gui_capability", null, TestGuiWrapper.class);
    }
}

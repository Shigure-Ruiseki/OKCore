package ruiseki.okcore.core.item.gui;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

public class CapabilityGuiTestWrapperConfig extends CapabilityConfig<GuiTestWrapper> {

    public static CapabilityGuiTestWrapperConfig _instance;

    @CapabilityInject(GuiTestWrapper.class)
    public static Capability<GuiTestWrapper> CAPABILITY = null;

    public CapabilityGuiTestWrapperConfig() {
        super(OKCore._instance, true, "gui_test_capability", null, GuiTestWrapper.class);
    }
}

package ruiseki.okcore.capabilities.item;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.item.IItemHandler;
import ruiseki.okcore.item.ItemStackHandler;
import ruiseki.okcore.test.BackpackProvider;

public class CapabilityItemHandler implements IInitListener {

    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

    @CapabilityInject(BackpackProvider.class)
    public static Capability<BackpackProvider> BACKPACK_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IItemHandler.class, ItemStackHandler::new);
        CapabilityManager.INSTANCE.register(BackpackProvider.class, () -> new BackpackProvider());
    }

    @Override
    public void onInit(IInitListener.Step initStep) {
        if (initStep == IInitListener.Step.PREINIT) {
            register();
        }
    }
}

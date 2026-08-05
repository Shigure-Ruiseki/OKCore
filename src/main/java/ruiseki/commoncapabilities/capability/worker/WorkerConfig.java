package ruiseki.commoncapabilities.capability.worker;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.commoncapabilities.api.capability.work.IWorker;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the worker capability.
 * 
 * @author rubensworks
 *
 */
public class WorkerConfig extends CapabilityConfig<IWorker> {

    /**
     * The unique instance.
     */
    public static WorkerConfig _instance;

    @CapabilityInject(IWorker.class)
    public static Capability<IWorker> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public WorkerConfig() {
        super(CommonCapabilities._instance, true, "worker", "Indication if a machine is working", IWorker.class);
    }
}

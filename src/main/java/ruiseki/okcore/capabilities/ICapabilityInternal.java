package ruiseki.okcore.capabilities;

import org.jetbrains.annotations.Nullable;

public interface ICapabilityInternal {

    @Nullable
    CapabilityDispatcher getCapabilities();
}

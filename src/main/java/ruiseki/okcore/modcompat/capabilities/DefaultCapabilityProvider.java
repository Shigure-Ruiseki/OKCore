package ruiseki.okcore.modcompat.capabilities;

import java.util.Objects;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;

/**
 * A default implementation of the capability provider.
 *
 * @author rubensworks
 */
public class DefaultCapabilityProvider<T> implements ICapabilityProvider {

    protected final ICapabilityTypeGetter<T> capabilityGetter;
    protected final T capability;
    protected final LazyOptional<T> capabilityOptional;

    public DefaultCapabilityProvider(ICapabilityTypeGetter<T> capabilityGetter, T capability) {
        this.capabilityGetter = Objects.requireNonNull(capabilityGetter);
        this.capability = Objects.requireNonNull(capability);
        this.capabilityOptional = LazyOptional.of(() -> this.capability);
    }

    public Capability<T> getCapabilityType() {
        return capabilityGetter.getCapability();
    }

    @Override
    public @NotNull <C> LazyOptional<C> getCapability(@NotNull Capability<C> cap, @Nullable ForgeDirection side) {
        if (cap == getCapabilityType()) {
            return this.capabilityOptional.cast();
        }
        return LazyOptional.empty();
    }
}

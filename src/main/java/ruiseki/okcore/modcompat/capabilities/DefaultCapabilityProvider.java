package ruiseki.okcore.modcompat.capabilities;

import java.util.Objects;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

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
    protected final LazyOptional<T> capability;

    public DefaultCapabilityProvider(ICapabilityTypeGetter<T> capabilityGetter, LazyOptional<T> capability) {
        this.capabilityGetter = Objects.requireNonNull(capabilityGetter);
        this.capability = Objects.requireNonNull(capability);
    }

    public DefaultCapabilityProvider(ICapabilityTypeGetter<T> capabilityGetter, T capability) {
        this(capabilityGetter, LazyOptional.of(() -> Objects.requireNonNull(capability)));
    }

    public DefaultCapabilityProvider(Capability<T> capabilityType, T capability) {
        Objects.requireNonNull(
            capabilityType,
            "The given capability can not be null, this is probably being called too early during init");
        this.capabilityGetter = () -> capabilityType;
        this.capability = LazyOptional.of(() -> Objects.requireNonNull(capability));
    }

    public Capability<T> getCapabilityType() {
        return Objects.requireNonNull(capabilityGetter.getCapability(), "A registered capability is null");
    }

    @Override
    public <T2> @NotNull LazyOptional<T2> getCapability(@NotNull Capability<T2> capability, ForgeDirection facing) {
        if (this.getCapabilityType() == Objects.requireNonNull(capability, "A given capability is null")) {
            return this.capability.cast();
        }
        return LazyOptional.empty();
    }
}

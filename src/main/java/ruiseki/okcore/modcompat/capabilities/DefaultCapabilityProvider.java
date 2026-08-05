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

    protected final Capability<T> capabilityType;
    protected final T capability;
    protected final LazyOptional<T> capabilityOptional;

    public DefaultCapabilityProvider(Capability<T> capabilityType, T capability) {
        this.capabilityType = Objects.requireNonNull(capabilityType);
        this.capability = Objects.requireNonNull(capability);
        this.capabilityOptional = LazyOptional.of(() -> this.capability);
    }

    @Override
    public @NotNull <C> LazyOptional<C> getCapability(@NotNull Capability<C> cap, @Nullable ForgeDirection side) {
        if (cap == this.capabilityType) {
            return this.capabilityOptional.cast();
        }
        return LazyOptional.empty();
    }
}

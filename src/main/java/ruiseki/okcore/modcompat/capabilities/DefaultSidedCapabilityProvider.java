package ruiseki.okcore.modcompat.capabilities;

import java.util.Objects;

import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.EnumFacingMap;
import ruiseki.okcore.datastructure.LazyOptional;

/**
 * A default sided implementation of the capability provider.
 *
 * @author rubensworks
 */
public class DefaultSidedCapabilityProvider<T> implements ICapabilityProvider {

    private final EnumFacingMap<Pair<Capability<T>, T>> capabilities;

    public DefaultSidedCapabilityProvider(EnumFacingMap<Pair<Capability<T>, T>> capabilities) {
        this.capabilities = Objects.requireNonNull(capabilities);
    }

    @Override
    public <T2> @NotNull LazyOptional<T2> getCapability(@NotNull Capability<T2> capability, ForgeDirection facing) {
        Pair<Capability<T>, T> value = capabilities.get(facing);
        if (value != null && Objects.requireNonNull(value.getKey(), "A registered capability is null")
            == Objects.requireNonNull(capability, "A given capability is null")) {
            return LazyOptional.of(value::getValue)
                .cast();
        }
        return LazyOptional.empty();
    }

    public static <T, H extends ISidedCapabilityConstructor<T>> EnumFacingMap<Pair<Capability<T>, T>> forAllSides(
        Capability<T> capabilityType, H constructor) {
        return EnumFacingMap.forAllValues(
            Pair.of(capabilityType, constructor.createForSide(ForgeDirection.DOWN)),
            Pair.of(capabilityType, constructor.createForSide(ForgeDirection.UP)),
            Pair.of(capabilityType, constructor.createForSide(ForgeDirection.NORTH)),
            Pair.of(capabilityType, constructor.createForSide(ForgeDirection.SOUTH)),
            Pair.of(capabilityType, constructor.createForSide(ForgeDirection.WEST)),
            Pair.of(capabilityType, constructor.createForSide(ForgeDirection.EAST)));
    }

    public static interface ISidedCapabilityConstructor<T> {

        public T createForSide(ForgeDirection side);
    }
}

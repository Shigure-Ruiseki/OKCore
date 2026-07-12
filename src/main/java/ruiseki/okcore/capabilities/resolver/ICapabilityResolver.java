package ruiseki.okcore.capabilities.resolver;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;

@NotNullByDefault
public interface ICapabilityResolver {

    /**
     * Gets the list of capabilities this resolver is able to resolve.
     *
     * @return List of capabilities this resolver can resolve.
     */
    List<Capability<?>> getSupportedCapabilities();

    /**
     * Resolves a given capability from a given side. This value should be cached for later invalidation, as well as
     * quicker re-lookup.
     *
     * @param capability Capability
     * @param side       Side
     *
     * @return LazyOptional for the given capability
     *
     * @apiNote This method should only be called with capabilities that are in {@link #getSupportedCapabilities()}
     * @implNote The result should be cached
     */
    <T> LazyOptional<T> resolve(Capability<T> capability, @Nullable ForgeDirection side);

    /**
     * Invalidates the given capability on the given side.
     *
     * @param capability Capability
     * @param side       Side
     */
    void invalidate(Capability<?> capability, @Nullable ForgeDirection side);

    /**
     * Invalidates all cached capabilities.
     */
    void invalidateAll();
}

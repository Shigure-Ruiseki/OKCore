package ruiseki.okcore.capabilities.resolver.manager;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.resolver.ICapabilityResolver;

@NotNullByDefault
public interface ICapabilityHandlerManager<CONTAINER> extends ICapabilityResolver {

    /**
     * Checks if the capability handler manager can handle this substance type.
     *
     * @return {@code true} if it can handle the substance type, {@code false} otherwise.
     */
    boolean canHandle();

    /**
     * Gets the containers for a given side.
     *
     * @param side The side
     *
     * @return Containers on the given side
     */
    List<CONTAINER> getContainers(@Nullable ForgeDirection side);
}

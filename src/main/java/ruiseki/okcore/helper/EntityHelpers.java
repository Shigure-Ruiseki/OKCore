package ruiseki.okcore.helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityInternal;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.entity.cooldown.ICooldownHandler;
import ruiseki.okcore.entity.cooldown.ItemCooldowns;

public class EntityHelpers {

    public static ItemCooldowns getItemCooldowns(EntityPlayer player) {
        if (player == null) return null;
        try {
            ICooldownHandler provider = (ICooldownHandler) (Object) player;

            return provider.getItemCooldowns();

        } catch (ClassCastException ignored) {
            return null;
        }
    }

    public static <T> LazyOptional<T> getCapability(Entity stack, @NotNull Capability<T> capability) {
        if (stack == null) return null;
        try {
            ICapabilityProvider provider = (ICapabilityProvider) (Object) stack;

            return provider.getCapability(capability);

        } catch (ClassCastException ignored) {
            return LazyOptional.empty();
        }
    }

    public static CapabilityDispatcher getCapabilities(Entity entity) {
        if (entity == null) return null;
        try {
            ICapabilityInternal provider = (ICapabilityInternal) (Object) entity;

            return provider.getCapabilities();

        } catch (ClassCastException ignored) {
            return null;
        }
    }
}

package ruiseki.okcore.helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityInternal;
import ruiseki.okcore.capabilities.ICapabilityProvider;
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

    public static <T> T getCapability(Entity stack, @NotNull Capability<T> capability,
        @Nullable ForgeDirection facing) {
        if (stack == null) return null;
        try {
            ICapabilityProvider provider = (ICapabilityProvider) (Object) stack;

            return provider.getCapability(capability, facing);

        } catch (ClassCastException ignored) {
            return null;
        }
    }

    public static boolean hasCapability(Entity stack, @NotNull Capability<?> capability,
        @Nullable ForgeDirection facing) {
        if (stack == null) return false;
        try {
            ICapabilityProvider provider = (ICapabilityProvider) (Object) stack;

            return provider.hasCapability(capability, facing);

        } catch (ClassCastException ignored) {
            return false;
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

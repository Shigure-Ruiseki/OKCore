package ruiseki.okcore.event;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;

public class OKEventFactory {

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(TileEntity tileEntity) {
        return gatherCapabilities(new AttachCapabilitiesEvent<TileEntity>(TileEntity.class, tileEntity), null);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(Entity entity) {
        return gatherCapabilities(new AttachCapabilitiesEvent<Entity>(Entity.class, entity), null);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(Village village) {
        return gatherCapabilities(new AttachCapabilitiesEvent<Village>(Village.class, village), null);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(ItemStack stack, ICapabilityProvider parent) {
        return gatherCapabilities(new AttachCapabilitiesEvent<ItemStack>(ItemStack.class, stack), parent);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(World world, ICapabilityProvider parent) {
        return gatherCapabilities(new AttachCapabilitiesEvent<World>(World.class, world), parent);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(Chunk chunk) {
        return gatherCapabilities(new AttachCapabilitiesEvent<Chunk>(Chunk.class, chunk), null);
    }

    @Nullable
    private static CapabilityDispatcher gatherCapabilities(AttachCapabilitiesEvent<?> event,
        @Nullable ICapabilityProvider parent) {
        MinecraftForge.EVENT_BUS.post(event);
        return !event.getCapabilities()
            .isEmpty() || parent != null ? new CapabilityDispatcher(event.getCapabilities(), parent) : null;
    }
}

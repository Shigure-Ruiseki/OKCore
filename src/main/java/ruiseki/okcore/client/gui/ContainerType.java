package ruiseki.okcore.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.registries.IForgeRegistry;
import ruiseki.okcore.registries.IForgeRegistryEntry;
import ruiseki.okcore.registries.RegistryBuilder;
import ruiseki.okcore.registries.RegistryEvent;

@EventBusSubscriber(phase = Phase.CONSTRUCT)
public class ContainerType<T extends ContainerExtended> implements IForgeRegistryEntry<ContainerType<?>> {

    public static IForgeRegistry<ContainerType<?>> REGISTRY;

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onRegistriesCreate(RegistryEvent.NewRegistry event) {
        REGISTRY = new RegistryBuilder<ContainerType<?>>().setName(new ResourceLocation("okcore", "container_type"))
            .setType((Class<ContainerType<?>>) (Class<?>) ContainerType.class)
            .create();
    }

    private ResourceLocation name;
    private final ContainerType.GuiSupplier<T> constructor;

    public ContainerType(@NotNull ContainerType.GuiSupplier<T> constructor) {
        this.constructor = constructor;
    }

    @Override
    public ContainerType<T> setRegistryName(ResourceLocation name) {
        this.name = name;
        return this;
    }

    @Override
    public @Nullable ResourceLocation getRegistryName() {
        return this.name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<ContainerType<?>> getRegistryType() {
        return (Class<ContainerType<?>>) (Class<?>) ContainerType.class;
    }

    public T create(int windowId, InventoryPlayer playerInv, ExtendedBuffer extraData) {
        return constructor.create(windowId, playerInv, extraData);
    }

    public T create(int windowId, InventoryPlayer playerInv) {
        return create(windowId, playerInv, null);
    }

    @FunctionalInterface
    public interface GuiSupplier<T extends ContainerExtended> {

        T create(int windowId, InventoryPlayer playerInv, ExtendedBuffer extraData);
    }
}

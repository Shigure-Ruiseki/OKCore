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
public class GuiType<T extends ContainerExtended> implements IForgeRegistryEntry<GuiType<?>> {

    public static IForgeRegistry<GuiType<?>> REGISTRY;

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onRegistriesCreate(RegistryEvent.NewRegistry event) {
        REGISTRY = new RegistryBuilder<GuiType<?>>().setName(new ResourceLocation("okcore", "guitype"))
            .setType((Class<GuiType<?>>) (Class<?>) GuiType.class)
            .create();
    }

    private ResourceLocation name;
    private final GuiType.GuiSupplier<T> constructor;

    public GuiType(@NotNull GuiType.GuiSupplier<T> constructor) {
        this.constructor = constructor;
    }

    @Override
    public GuiType<T> setRegistryName(ResourceLocation name) {
        this.name = name;
        return this;
    }

    @Override
    public @Nullable ResourceLocation getRegistryName() {
        return this.name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<GuiType<?>> getRegistryType() {
        return (Class<GuiType<?>>) (Class<?>) GuiType.class;
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

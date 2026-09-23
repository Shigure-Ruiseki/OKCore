package ruiseki.okcore.client.gui;

import java.util.Map;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.ExtendedBuffer;

@SideOnly(Side.CLIENT)
public class GuiScreens {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<GuiType<?>, ScreenConstructor<?, ?>> SCREENS = Maps.newHashMap();

    public static <T extends ContainerExtended> void create(GuiType<T> type, Minecraft mc, int windowId,
        ExtendedBuffer extraData) {
        getScreenFactory(type, extraData).ifPresent(factory -> factory.fromPacket(type, windowId, mc, extraData));
    }

    public static <T extends ContainerExtended> Optional<ScreenConstructor<T, ?>> getScreenFactory(GuiType<T> type,
        ExtendedBuffer extraData) {
        if (type == null) {
            LOGGER.warn("Trying to open invalid screen for null GuiType");
        } else {
            ScreenConstructor<T, ?> constructor = getConstructor(type);
            if (constructor == null) {
                LOGGER.warn("Failed to create screen for GuiType: {}", type.getRegistryName());
            } else {
                return Optional.of(constructor);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    private static <T extends ContainerExtended> ScreenConstructor<T, ?> getConstructor(GuiType<T> type) {
        return (ScreenConstructor<T, ?>) SCREENS.get(type);
    }

    public static <T extends ContainerExtended, U extends GuiScreen & IContainerAccess<T>> void register(
        GuiType<T> type, ScreenConstructor<T, U> factory) {
        ScreenConstructor<?, ?> existing = SCREENS.put(type, factory);
        if (existing != null) {
            throw new IllegalStateException("Duplicate registration for " + type.getRegistryName());
        }
    }

    public static boolean selfTest() {
        boolean missingScreens = false;
        if (GuiType.REGISTRY != null) {
            for (GuiType<?> type : GuiType.REGISTRY) {
                if (!SCREENS.containsKey(type)) {
                    LOGGER.debug("GuiType {} has no matching screen constructor", type.getRegistryName());
                    missingScreens = true;
                }
            }
        }
        return missingScreens;
    }

    @SideOnly(Side.CLIENT)
    public interface ScreenConstructor<T extends ContainerExtended, U extends GuiScreen & IContainerAccess<T>> {

        default void fromPacket(GuiType<T> type, int windowId, Minecraft mc, ExtendedBuffer extraData) {
            InventoryPlayer inventoryPlayer = mc.thePlayer.inventory;

            if (extraData != null) {
                extraData.readerIndex(0);
            }
            T container = type.create(windowId, inventoryPlayer, extraData);

            if (extraData != null) {
                extraData.readerIndex(0);
            }
            U screen = this.create(container, inventoryPlayer, extraData);

            mc.thePlayer.openContainer = screen.getContainer();
            mc.displayGuiScreen(screen);
        }

        U create(T container, InventoryPlayer inventoryPlayer, ExtendedBuffer extraData);
    }
}

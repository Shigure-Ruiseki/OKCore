
package ruiseki.okcore.client.gui;

import java.util.Map;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.inventory.container.ContainerExtended;

/**
 * Client-side registry and factory manager for container GUI screens.
 *
 * <p>
 * This class associates registered {@link ContainerType} instances with
 * {@link ScreenConstructor} factories responsible for creating their
 * corresponding {@link GuiScreen} implementations.
 * </p>
 *
 * <p>
 * Screen factories are registered through
 * {@link #register(ContainerType, ScreenConstructor)} and retrieved
 * through {@link #getScreenFactory(ContainerType, Minecraft, int)}.
 * </p>
 *
 * <p>
 * When a GUI is opened from a network packet, the registered factory
 * creates the client-side container and screen, assigns the container
 * to the player, synchronizes the window identifier, and displays
 * the resulting GUI.
 * </p>
 *
 * <p>
 * Missing registrations are reported through the logger. The
 * {@link #selfTest()} method can be used to identify container types
 * without corresponding screen factories.
 * </p>
 *
 * <p>
 * This class is intended for client-side use only.
 * </p>
 *
 * @see ContainerType
 * @see ScreenConstructor
 * @see ContainerExtended
 * @see IContainerAccess
 */
@SideOnly(Side.CLIENT)
public class GuiScreens {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ContainerType<?>, ScreenConstructor<?, ?>> SCREENS = Maps.newHashMap();

    /**
     * Creates and opens a GUI screen for the specified container type.
     *
     * <p>
     * The registered screen factory is resolved using
     * {@link #getScreenFactory(ContainerType, Minecraft, int)}.
     * If a factory is found, its
     * {@link ScreenConstructor#fromPacket(ContainerType, Minecraft, int)}
     * method is invoked.
     * </p>
     *
     * <p>
     * If the container type is {@code null} or no screen factory has
     * been registered, a warning is logged and no screen is opened.
     * </p>
     *
     * @param <T>      the type of container associated with the screen
     * @param type     the registered container type
     * @param mc       the Minecraft client instance
     * @param windowId the window identifier assigned to the container
     *
     * @see #register(ContainerType, ScreenConstructor)
     */
    public static <T extends ContainerExtended> void create(ContainerType<T> type, Minecraft mc, int windowId) {
        getScreenFactory(type, mc, windowId).ifPresent(factory -> factory.fromPacket(type, mc, windowId));
    }

    /**
     * Retrieves the registered screen factory for the specified container type.
     *
     * <p>
     * If the container type is {@code null} or has no registered
     * screen constructor, this method logs a warning and returns
     * an empty {@link Optional}.
     * </p>
     *
     * <p>
     * The Minecraft instance and window identifier are accepted
     * for consistency with the screen creation API but are not
     * directly used by the lookup implementation.
     * </p>
     *
     * @param <T>      the container type
     * @param type     the container type whose factory should be retrieved
     * @param mc       the Minecraft client instance
     * @param windowId the window identifier associated with the request
     * @return an optional containing the registered screen factory,
     *         or an empty optional if no factory is available
     *
     * @see #register(ContainerType, ScreenConstructor)
     */
    public static <T extends ContainerExtended> Optional<ScreenConstructor<T, ?>> getScreenFactory(
        ContainerType<T> type, Minecraft mc, int windowId) {
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

    /**
     * Looks up the screen constructor associated with a container type.
     *
     * <p>
     * The returned constructor is cast to match the requested
     * container type. Type correctness relies on factories being
     * registered with the appropriate {@link ContainerType}.
     * </p>
     *
     * @param <T>  the container type
     * @param type the container type to look up
     * @return the registered screen constructor, or {@code null}
     *         if no matching registration exists
     */
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    private static <T extends ContainerExtended> ScreenConstructor<T, ?> getConstructor(ContainerType<T> type) {
        return (ScreenConstructor<T, ?>) SCREENS.get(type);
    }

    /**
     * Registers a screen constructor for the specified container type.
     *
     * <p>
     * The factory must create a {@link GuiScreen} that implements
     * {@link IContainerAccess} for the corresponding container type.
     * </p>
     *
     * <p>
     * Each container type is expected to have a single screen factory.
     * Registering the same type more than once causes an
     * {@link IllegalStateException}.
     * </p>
     *
     * <p>
     * Registrations should be performed during client initialization
     * before any GUI opening packets are processed.
     * </p>
     *
     * @param <T>     the container type
     * @param <U>     the GUI screen type
     * @param type    the container type to register
     * @param factory the constructor responsible for creating the screen
     * @throws IllegalStateException if a factory was already registered
     *                               for the specified container type
     *
     * @see ScreenConstructor
     */
    public static <T extends ContainerExtended, U extends GuiScreen & IContainerAccess<T>> void register(
        ContainerType<T> type, ScreenConstructor<T, U> factory) {
        ScreenConstructor<?, ?> existing = SCREENS.put(type, factory);
        if (existing != null) {
            throw new IllegalStateException("Duplicate registration for " + type.getRegistryName());
        }
    }

    /**
     * Checks whether registered container types have matching GUI factories.
     *
     * <p>
     * Iterates over {@link ContainerType#REGISTRY} and checks whether
     * each container type has an associated screen constructor.
     * Missing registrations are logged at debug level.
     * </p>
     *
     * <p>
     * If the container type registry is {@code null}, no checks
     * are performed and this method returns {@code false}.
     * </p>
     *
     * @return {@code true} if at least one registered container type
     *         has no matching screen constructor, otherwise {@code false}
     */
    public static boolean selfTest() {
        boolean missingScreens = false;
        if (ContainerType.REGISTRY != null) {
            for (ContainerType<?> type : ContainerType.REGISTRY) {
                if (!SCREENS.containsKey(type)) {
                    LOGGER.debug("GuiType {} has no matching screen constructor", type.getRegistryName());
                    missingScreens = true;
                }
            }
        }
        return missingScreens;
    }

    /**
     * Factory interface for creating client-side GUI screens associated
     * with a specific container type.
     *
     * <p>
     * Implementations construct a screen using the provided container
     * and player inventory. The default
     * {@link #fromPacket(ContainerType, Minecraft, int)} method handles
     * creation and display when opening a GUI from a network packet.
     * </p>
     *
     * <p>
     * This interface is a functional interface and can be implemented
     * using a lambda expression or constructor reference.
     * </p>
     *
     * <pre>
     * {@code
     * GuiScreens.register(MY_CONTAINER_TYPE, MyContainerScreen::new);
     * }
     * </pre>
     *
     * @param <T> the container type associated with the screen
     * @param <U> the screen type, which must extend {@link GuiScreen}
     *            and implement {@link IContainerAccess} for {@code T}
     *
     * @see ScreenFactorySafe
     * @see IContainerAccess
     */
    @SideOnly(Side.CLIENT)
    public interface ScreenConstructor<T extends ContainerExtended, U extends GuiScreen & IContainerAccess<T>> {

        /**
         * Creates and displays a GUI screen in response to a GUI opening packet.
         *
         * <p>
         * A new container is created from the registered
         * {@link ContainerType} using the supplied window identifier
         * and the current player's inventory.
         * </p>
         *
         * <p>
         * The screen is constructed through
         * {@link #create(ContainerExtended, InventoryPlayer)}.
         * Its container is then assigned to the player's
         * {@code openContainer}, and the provided window identifier
         * is applied before displaying the screen.
         * </p>
         *
         * <p>
         * The screen is displayed through
         * {@link FMLCommonHandler#showGuiScreen(Object)}.
         * </p>
         *
         * @param type     the container type used to create the container
         * @param mc       the Minecraft client instance
         * @param windowId the window identifier received from the server
         */
        default void fromPacket(ContainerType<T> type, Minecraft mc, int windowId) {
            InventoryPlayer inventoryPlayer = mc.thePlayer.inventory;
            U screen = this.create(type.create(windowId, inventoryPlayer), inventoryPlayer);
            mc.thePlayer.openContainer = screen.getContainer();
            mc.thePlayer.openContainer.windowId = windowId;
            FMLCommonHandler.instance()
                .showGuiScreen(screen);
        }

        /**
         * Creates a GUI screen for the specified container and inventory.
         *
         * <p>
         * Implementations should construct and return a screen that
         * exposes the supplied container through
         * {@link IContainerAccess#getContainer()}.
         * </p>
         *
         * <p>
         * This method does not display the screen or assign its
         * window identifier. Those operations are performed by
         * {@link #fromPacket(ContainerType, Minecraft, int)}.
         * </p>
         *
         * @param container       the container associated with the screen
         * @param inventoryPlayer the player's inventory
         * @return the newly constructed GUI screen
         */
        U create(T container, InventoryPlayer inventoryPlayer);
    }
}

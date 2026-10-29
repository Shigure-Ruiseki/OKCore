
package ruiseki.okcore.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.inventory.container.ContainerExtended;

/**
 * A type-safe wrapper for {@link GuiScreens.ScreenConstructor} that simplifies
 * the creation of container GUI screens using lambdas and constructor references.
 *
 * <p>
 * This factory supports GUI screens that extend {@link GuiScreen} and implement
 * {@link IContainerAccess} for a specific {@link ContainerExtended} type.
 * </p>
 *
 * <p>
 * The additional generic constraints allow Java to infer the screen type
 * from a constructor reference, avoiding the need for explicit generic
 * arguments or casts at the call site.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>
 * 
 * {
 *     &#64;code
 *     ScreenFactorySafe<MyContainer, MyScreen> factory = new ScreenFactorySafe<>(MyScreen::new);
 *
 *     MyScreen screen = factory.create(container, player.inventory);
 * }
 * </pre>
 *
 * <p>
 * The factory delegates screen creation to the supplied
 * {@link GuiScreens.ScreenConstructor}.
 * It does not manage screen initialization, display, or lifecycle.
 * </p>
 *
 * <p>
 * This class is intended for client-side use only.
 * </p>
 *
 * @param <T> the container type associated with the GUI screen
 * @param <U> the GUI screen type, which must extend {@link GuiScreen}
 *            and implement {@link IContainerAccess} for {@code T}
 *
 * @see GuiScreens.ScreenConstructor
 * @see ContainerExtended
 * @see IContainerAccess
 */
@SideOnly(Side.CLIENT)
public class ScreenFactorySafe<T extends ContainerExtended, U extends GuiScreen & IContainerAccess<T>>
    implements GuiScreens.ScreenConstructor<T, U> {

    private final GuiScreens.ScreenConstructor<T, ?> screenFactoryInner;

    /**
     * Constructs a type-safe screen factory using the supplied screen constructor.
     *
     * <p>
     * The constructor accepts any screen type that extends {@link GuiScreen}
     * and implements {@link IContainerAccess} for the specified container type.
     * </p>
     *
     * <p>
     * The supplied factory is stored and invoked when
     * {@link #create(ContainerExtended, InventoryPlayer)} is called.
     * </p>
     *
     * @param screenFactoryInner the underlying screen constructor
     *                           responsible for creating GUI instances
     * @param <U2>               the concrete screen type produced by the underlying factory
     */
    public <U2 extends GuiScreen & IContainerAccess<T>> ScreenFactorySafe(
        GuiScreens.ScreenConstructor<T, U2> screenFactoryInner) {
        this.screenFactoryInner = screenFactoryInner;
    }

    /**
     * Creates a GUI screen for the specified container and player inventory.
     *
     * <p>
     * Screen construction is delegated to the underlying factory.
     * The resulting instance is cast to the declared screen type {@code U}.
     * </p>
     *
     * <p>
     * The underlying factory must produce a screen compatible with {@code U}.
     * An incompatible result may cause a {@link ClassCastException}
     * when the caller uses the returned value.
     * </p>
     *
     * @param container       the container associated with the screen
     * @param inventoryPlayer the player's inventory
     * @return the newly created GUI screen
     */
    @Override
    @SuppressWarnings("unchecked")
    public U create(T container, InventoryPlayer inventoryPlayer) {
        return (U) this.screenFactoryInner.create(container, inventoryPlayer);
    }
}

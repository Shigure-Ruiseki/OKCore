package ruiseki.okcore.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.inventory.container.ContainerExtended;

/**
 * A type-safe implementation of {@link GuiScreens.ScreenConstructor}.
 * This enables more convenient syntax via lambdas than the default {@link GuiScreens.ScreenConstructor}.
 *
 * For example: `new ScreenFactorySafe(ContainerScreenAbilityContainer::new)`.
 *
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
@SuppressWarnings("unchecked")
public class ScreenFactorySafe<T extends ContainerExtended, U extends GuiScreen & IContainerAccess<T>>
    implements GuiScreens.ScreenConstructor<T, U> {

    private final GuiScreens.ScreenConstructor<T, ?> screenFactoryInner;

    public <U2 extends GuiScreen & IContainerAccess<T>> ScreenFactorySafe(
        GuiScreens.ScreenConstructor<T, U2> screenFactoryInner) {
        this.screenFactoryInner = screenFactoryInner;
    }

    @Override
    public U create(T container, InventoryPlayer inventoryPlayer) {
        return (U) this.screenFactoryInner.create(container, inventoryPlayer);
    }
}

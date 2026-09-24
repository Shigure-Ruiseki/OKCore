package ruiseki.okcore.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * A type-safe implementation of {@link GuiScreens.ScreenConstructor}.
 * This enables more convenient syntax via lambdas than the default {@link GuiScreens.ScreenConstructor}.
 *
 * For example: `new ScreenFactorySafe(ContainerScreenAbilityContainer::new)`.
 *
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class ScreenFactorySafe<T extends ContainerExtended, U1 extends GuiScreen & IContainerAccess<T>, U2 extends GuiScreen & IContainerAccess<T>>
    implements GuiScreens.ScreenConstructor<T, U1> {

    private final GuiScreens.ScreenConstructor<T, U2> screenFactoryInner;

    public ScreenFactorySafe(GuiScreens.ScreenConstructor<T, U2> screenFactoryInner) {
        this.screenFactoryInner = screenFactoryInner;
    }

    @Override
    public U1 create(T container, InventoryPlayer inventoryPlayer, ExtendedBuffer extraData) {
        return (U1) this.screenFactoryInner.create(container, inventoryPlayer, extraData);
    }
}

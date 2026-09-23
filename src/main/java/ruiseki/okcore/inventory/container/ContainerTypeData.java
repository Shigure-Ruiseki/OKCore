package ruiseki.okcore.inventory.container;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.client.gui.GuiType;

public class ContainerTypeData<T extends ContainerExtended> extends GuiType<T> {

    public ContainerTypeData(@NotNull GuiType.GuiSupplier<T> constructor) {
        super(constructor);
    }
}

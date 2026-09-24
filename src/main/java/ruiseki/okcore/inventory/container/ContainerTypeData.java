package ruiseki.okcore.inventory.container;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.client.gui.ContainerType;

public class ContainerTypeData<T extends ContainerExtended> extends ContainerType<T> {

    public ContainerTypeData(@NotNull ContainerType.GuiSupplier<T> constructor) {
        super(constructor);
    }
}

package ruiseki.okcore.core;

import java.util.function.Supplier;

import net.minecraft.item.Item;

import ruiseki.okcore.Reference;
import ruiseki.okcore.config.ModConfig;
import ruiseki.okcore.item.IItem;
import ruiseki.okcore.registries.DeferredRegister;
import ruiseki.okcore.registries.RegistryObject;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.test.ItemEnergyTest;
import ruiseki.okcore.test.ItemFluidTest;
import ruiseki.okcore.test.ItemItemTest;

public final class OKCoreItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Reference.MOD_ID);

    public static final RegistryObject<Item> ITEM_TEST = register(
        "item_test",
        () -> ModConfig.useItemTest,
        ItemItemTest::new);

    public static final RegistryObject<Item> ENERGY_TEST = register(
        "energy_test",
        () -> ModConfig.useItemTest,
        ItemEnergyTest::new);

    public static final RegistryObject<Item> FLUID_TEST = register(
        "fluid_test",
        () -> ModConfig.useItemTest,
        ItemFluidTest::new);

    private static RegistryObject<Item> register(String name, Supplier<Boolean> configCondition,
        Supplier<IItem> itemSupplier) {
        if (!configCondition.get()) {
            return RegistryObject.empty();
        }

        return ITEMS.register(
            name,
            () -> itemSupplier.get()
                .get());
    }

    public static void register() {
        ITEMS.register();
    }

    private OKCoreItems() {}
}

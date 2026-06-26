package ruiseki.okcore.core;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.Level;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.ModConfig;
import ruiseki.okcore.item.IItem;
import ruiseki.okcore.test.ItemEnergyTest;
import ruiseki.okcore.test.ItemItemTest;

public enum ModItems {

    // spotless: off

    ITEM_TEST(ModConfig.useItemTest, new ItemItemTest()),
    ENERGY_TEST(ModConfig.useItemTest, new ItemEnergyTest()),

    ;

    // spotless: on
    public static final ModItems[] VALUES = values();

    public static void preInit() {
        for (ModItems item : VALUES) {
            if (item.item == null || !item.enable) {
                continue;
            }
            try {
                item.item.init();
                OKCore.okLog(Level.INFO, "Successfully initialized " + item.name());
            } catch (Exception e) {
                OKCore.okLog(Level.ERROR, "Failed to initialize item: +" + item.name());
            }
        }
    }

    private final IItem item;
    private final boolean enable;

    ModItems(IItem block) {
        this(true, block);
    }

    ModItems(boolean enable, IItem block) {
        this.item = block;
        this.enable = enable;
    }

    public Item getItem() {
        return item.getItem();
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return item != null ? new ItemStack(this.getItem(), count, meta) : null;
    }
}

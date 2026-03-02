package ruiseki.okcore.test;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.Level;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.item.IItem;

public enum OKCoreItems {

    ITEM(new ItemTest()),

    ;

    public static final OKCoreItems[] VALUES = values();

    public static void preInit() {
        if (!MinecraftHelpers.isModdedEnvironment()) return;
        for (OKCoreItems value : VALUES) {
            try {
                value.item.init();
                OKCore.okLog(Level.INFO, "Successfully initialized " + value.name());
            } catch (Exception e) {
                OKCore.okLog(Level.ERROR, "Failed to initialize value: +" + value.name());
            }
        }
    }

    private final IItem item;

    OKCoreItems(IItem item) {
        this.item = item;
    }

    public Item getItem() {
        return item.getItem();
    }

    public String getName() {
        return getItem().getUnlocalizedName()
            .replace("item.", "");
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.getItem(), count, meta);
    }
}

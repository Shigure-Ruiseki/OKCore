package ruiseki.okcore.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;

public class ItemFoodOK extends ItemFood implements IItem {

    private final String name;

    public ItemFoodOK(String name, int amount, float saturation, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
        this.name = name;
        this.setUnlocalizedName(this.name);
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

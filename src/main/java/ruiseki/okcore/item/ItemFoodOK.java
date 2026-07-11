package ruiseki.okcore.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;

public class ItemFoodOK extends ItemFood implements IItem {

    public ItemFoodOK(int amount, float saturation, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
    }

    @Override
    public Item getItem() {
        return this;
    }
}

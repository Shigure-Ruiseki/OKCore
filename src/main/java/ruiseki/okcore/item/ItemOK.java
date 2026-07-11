package ruiseki.okcore.item;

import net.minecraft.item.Item;

import ruiseki.okcore.capabilities.IItemCapability;

public class ItemOK extends Item implements IItem, IItemCapability, IItemSharedTag {

    public ItemOK() {
        super();
    }

    @Override
    public Item get() {
        return this;
    }
}

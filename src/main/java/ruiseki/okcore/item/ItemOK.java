package ruiseki.okcore.item;

import net.minecraft.item.Item;

import ruiseki.okcore.capabilities.IItemCapability;

public class ItemOK extends Item implements IItem, IItemCapability, IItemSharedTag {

    private final String name;

    public ItemOK(String name) {
        super();
        this.name = name;
        setUnlocalizedName(name);
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public String getName() {
        return name;
    }
}

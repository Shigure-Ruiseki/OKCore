package ruiseki.okcore.item;

import net.minecraft.item.Item;

@Deprecated
public class ItemOK extends ItemBase implements IItem {

    public ItemOK() {
        super();
    }

    @Override
    public Item get() {
        return this;
    }
}

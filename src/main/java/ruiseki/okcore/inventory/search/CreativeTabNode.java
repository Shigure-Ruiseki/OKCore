package ruiseki.okcore.inventory.search;

import net.minecraft.item.Item;

import ruiseki.okcore.inventory.ItemStackKey;

final class CreativeTabNode implements SearchNode {

    private final String tab;

    CreativeTabNode(String tab) {
        this.tab = tab;
    }

    @Override
    public boolean matches(ItemStackKey k) {
        Item item = k.getItem();
        if (item == null || item.getCreativeTab() == null) return false;

        return item.getCreativeTab()
            .getTabLabel()
            .toLowerCase()
            .contains(tab.toLowerCase());
    }
}

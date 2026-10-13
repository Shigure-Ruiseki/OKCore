package ruiseki.okcore.inventory.search;

import ruiseki.okcore.inventory.ItemStackKey;

import java.util.List;

final class TrueNode implements SearchNode {

    public static TrueNode INSTANCE = new TrueNode();

    TrueNode() {
    }

    @Override
    public boolean matches(ItemStackKey k) {
        return true;
    }
}

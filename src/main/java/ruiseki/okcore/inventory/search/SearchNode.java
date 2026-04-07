package ruiseki.okcore.inventory.search;

import ruiseki.okcore.inventory.ItemStackKey;

public interface SearchNode {

    boolean matches(ItemStackKey key);
}

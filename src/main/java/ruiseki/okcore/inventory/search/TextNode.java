package ruiseki.okcore.inventory.search;

import ruiseki.okcore.inventory.ItemStackKey;

final class TextNode implements SearchNode {

    private final String query;

    TextNode(String query) {
        this.query = query.toLowerCase();
    }

    @Override
    public boolean matches(ItemStackKey k) {
        return k.getDisplayName()
            .contains(query);
    }
}

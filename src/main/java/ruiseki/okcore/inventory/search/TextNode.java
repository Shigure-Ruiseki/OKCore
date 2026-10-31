package ruiseki.okcore.inventory.search;

import java.util.Locale;

import ruiseki.okcore.inventory.ItemStackKey;

final class TextNode implements SearchNode {

    private final String query;

    TextNode(String query) {
        this.query = query.toLowerCase();
    }

    @Override
    public boolean matches(ItemStackKey key) {
        return key != null && key.getDisplayName() != null
            && key.getDisplayName()
                .toLowerCase(Locale.ROOT)
                .contains(query);
    }
}

package ruiseki.okcore.inventory.search;

import ruiseki.okcore.inventory.ItemStackKey;

final class ModNode implements SearchNode {

    private final String mod;

    ModNode(String mod) {
        this.mod = mod;
    }

    @Override
    public boolean matches(ItemStackKey k) {
        return k.getModId()
            .contains(mod);
    }
}

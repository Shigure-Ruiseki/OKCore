package ruiseki.okcore.config.extendedconfig;

import net.minecraft.item.Item;

import ruiseki.okcore.config.OKCoreConfigException;
import ruiseki.okcore.init.IObjectReference;

/**
 * Reference to an item.
 *
 * @author rubensworks
 */
public class ItemConfigReference implements IObjectReference<Item> {

    private final Class<? extends ItemConfig> itemConfigClass;
    private ItemConfig itemConfig = null;

    public ItemConfigReference(Class<? extends ItemConfig> itemConfigClass) {
        this.itemConfigClass = itemConfigClass;
    }

    @Override
    public Item getObject() {
        if (itemConfig == null) {
            try {
                itemConfig = (ItemConfig) itemConfigClass.getField("_instance")
                    .get(null);
            } catch (IllegalAccessException | NoSuchFieldException | ClassCastException e) {
                e.printStackTrace();
                throw new OKCoreConfigException(
                    "Something went wrong while materializating the reference to " + itemConfigClass.getName());
            }
        }
        return itemConfig.getInstance();
    }
}

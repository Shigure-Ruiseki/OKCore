package ruiseki.okcore.config.configurabletypeaction;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.client.gui.GuiHandler;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * The action used for {@link ItemConfig}.
 *
 * @author rubensworks
 * @see ConfigurableTypeAction
 */
public class ItemAction extends ConfigurableTypeAction<ItemConfig> {

    /**
     * Registers an item.
     *
     * @param item         The item instance.
     * @param name         The unique name for this block.
     * @param creativeTabs The creative tab this block will reside in.
     */
    public static void register(Item item, String name, @Nullable CreativeTabs creativeTabs) {
        GameRegistry.registerItem(item, name);

        if (creativeTabs != null) {
            item.setCreativeTab(creativeTabs);
        }
    }

    @Override
    public void preRun(ItemConfig eConfig, Configuration config, boolean startup) {
        // Get property in config file and set comment
        Property property = config.get(
            eConfig.getHolderType()
                .getCategory(),
            eConfig.getNamedId(),
            eConfig.isEnabled());
        property.setRequiresMcRestart(true);
        property.comment = eConfig.getComment();

        if (startup) {
            // Update the ID, it could've changed
            eConfig.setEnabled(property.getBoolean(true));
        }
    }

    @Override
    public void postRun(ItemConfig eConfig, Configuration config) {
        // Save the config inside the correct element
        eConfig.save();

        Item item = (Item) eConfig.getSubInstance();

        // Register item and set creative tab.
        register(item, eConfig.getSubUniqueName(), eConfig.getTargetTab());

        // Optionally register gui
        if (item instanceof IGuiContainerProvider) {
            IGuiContainerProvider gui = (IGuiContainerProvider) item;
            eConfig.getMod()
                .getGuiHandler()
                .registerGUI(gui, GuiHandler.GuiType.ITEM);
        }
    }
}

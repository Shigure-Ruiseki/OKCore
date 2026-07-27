package ruiseki.okcore.config.configurabletypeaction;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.client.gui.GuiHandler;
import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.config.configurable.ConfigurableBlockContainer;
import ruiseki.okcore.config.configurable.IConfigurableBlock;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * The action used for {@link BlockConfig}.
 *
 * @author rubensworks
 * @see ConfigurableTypeAction
 */
public class BlockAction extends ConfigurableTypeAction<BlockConfig> {

    /**
     * Registers a block.
     *
     * @param block        The block instance.
     * @param name         The unique name for this block.
     * @param creativeTabs The creative tab this block will reside in.
     */
    public static void register(Block block, String name, @Nullable CreativeTabs creativeTabs) {
        register(block, null, name, creativeTabs);
    }

    /**
     * Registers a block.
     *
     * @param block          The block instance.
     * @param itemBlockClass The optional item block class.
     * @param name           The unique name for this block.
     * @param creativeTabs   The creative tab this block will reside in.
     */
    public static void register(Block block, @Nullable Class<? extends ItemBlock> itemBlockClass, String name,
        @Nullable CreativeTabs creativeTabs) {
        if (itemBlockClass == null) {
            GameRegistry.registerBlock(block, name);
        } else {
            GameRegistry.registerBlock(block, itemBlockClass, name);
        }

        if (creativeTabs != null) {
            block.setCreativeTab(creativeTabs);
        }
    }

    @Override
    public void preRun(BlockConfig eConfig, Configuration config, boolean startup) {
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
    public void postRun(BlockConfig eConfig, Configuration config) {
        // Save the config inside the correct element
        eConfig.save();

        Block block = (Block) eConfig.getSubInstance();

        // Register block and set creative tab.
        register(block, eConfig.getItemBlockClass(), eConfig.getSubUniqueName(), eConfig.getTargetTab());

        // Also register tile entity
        GuiHandler.GuiType guiType = GuiHandler.GuiType.BLOCK;
        if (eConfig.getHolderType()
            .equals(ConfigurableType.BLOCKCONTAINER)) {
            ConfigurableBlockContainer container = (ConfigurableBlockContainer) block;
            // This alternative registration is required to remain compatible with old worlds.
            GameRegistry.registerTileEntityWithAlternatives(
                container.getTileEntity(),
                eConfig.getMod()
                    .getModId() + ":"
                    + eConfig.getSubUniqueName(),
                eConfig.getSubUniqueName());
            guiType = GuiHandler.GuiType.TILE;
        }

        // If the block has a GUI, go ahead and register that.
        if (block instanceof IConfigurableBlock && ((IConfigurableBlock) block).hasGui()) {
            IGuiContainerProvider gui = (IGuiContainerProvider) block;
            eConfig.getMod()
                .getGuiHandler()
                .registerGUI(gui, guiType);
        }

        // Register optional ore dictionary ID
        if (eConfig.getOreDictionaryId() != null) {
            OreDictionary.registerOre(eConfig.getOreDictionaryId(), new ItemStack((Block) eConfig.getSubInstance()));
        }
    }

}

package ruiseki.okcore.config.configurabletypeaction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.client.gui.GuiHandler;
import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.config.configurable.ConfigurableBlockContainer;
import ruiseki.okcore.config.configurable.IConfigurableBlock;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
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
     * @param config       The config.
     * @param creativeTabs The creative tab this block will reside in.
     */
    public static void register(Block block, ExtendedConfig<BlockConfig> config, @Nullable CreativeTabs creativeTabs) {
        register(block, null, config, creativeTabs);
    }

    /**
     * Registers a block.
     *
     * @param block        The block instance.
     * @param itemclass    The optional item block class.
     * @param config       The config.
     * @param creativeTabs The creative tab this block will reside in.
     */
    @SuppressWarnings("unchecked")
    public static void register(Block block, @Nullable Class<? extends Item> itemclass,
        ExtendedConfig<BlockConfig> config, @Nullable CreativeTabs creativeTabs) {
        String name = config.getSubUniqueName();
        if (itemclass == null) {
            GameRegistry.registerBlock(block, null, name);
        } else if (ItemBlock.class.isAssignableFrom(itemclass)) {
            GameRegistry.registerBlock(block, (Class<? extends ItemBlock>) itemclass, name);
        } else {
            GameRegistry.registerBlock(block, null, name);
            try {
                Constructor<? extends Item> itemConstructor = itemclass.getConstructor(Block.class);
                Item item = itemConstructor.newInstance(block);
                GameRegistry.registerItem(item, name);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        if (creativeTabs != null) {
            block.setCreativeTab(creativeTabs);
        }
    }

    @Override
    public void preRun(BlockConfig eConfig, Configuration config, boolean startup) {
        Property property = config.get(
            eConfig.getHolderType()
                .getCategory(),
            eConfig.getNamedId(),
            eConfig.isEnabled());
        property.setRequiresMcRestart(true);
        property.comment = eConfig.getComment();

        if (startup) {
            eConfig.setEnabled(property.getBoolean(true));
        }
    }

    @Override
    public void postRun(BlockConfig eConfig, Configuration config) {
        eConfig.save();

        Block block = (Block) eConfig.getSubInstance();

        register(block, eConfig.getItemBlockClass(), eConfig, eConfig.getTargetTab());

        GuiHandler.GuiType guiType = GuiHandler.GuiType.BLOCK;
        if (eConfig.getHolderType()
            .equals(ConfigurableType.BLOCKCONTAINER)) {
            ConfigurableBlockContainer container = (ConfigurableBlockContainer) block;
            GameRegistry.registerTileEntityWithAlternatives(
                container.getTileEntity(),
                eConfig.getMod()
                    .getModId() + ":"
                    + eConfig.getSubUniqueName(),
                eConfig.getSubUniqueName());
            guiType = GuiHandler.GuiType.TILE;
        }

        if (block instanceof IConfigurableBlock configurableBlock && configurableBlock.hasGui()) {
            IGuiContainerProvider gui = (IGuiContainerProvider) block;
            eConfig.getMod()
                .getGuiHandler()
                .registerGUI(gui, guiType);
        }

        if (block instanceof IBlockPropertyProvider provider) {
            provider.registerProperties();
        }

        if (eConfig.getOreDictionaryId() != null) {
            OreDictionary.registerOre(eConfig.getOreDictionaryId(), new ItemStack(block));
        }
    }
}

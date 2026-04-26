package ruiseki.okcore.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.item.ItemBlockOK;
import ruiseki.okcore.recipe.IOreDictEntry;

public interface IBlock {

    Block getBlock();

    boolean isHasSubtypes();

    String getName();

    default void init() {
        registerBlock();
        registerTileEntity();
        registerComponent();
    }

    default void registerBlock() {
        GameRegistry.registerBlock(this.getBlock(), getItemBlockClass(), getName(), getItemBlockArgs());
    }

    default void registerTileEntity() {}

    default void registerComponent() {
        if (this instanceof IOreDictEntry oreDictEntry) oreDictEntry.registerOreDict();
    }

    default Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockOK.class;
    }

    default Object[] getItemBlockArgs() {
        return new Object[0];
    }
}

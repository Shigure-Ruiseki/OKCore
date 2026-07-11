package ruiseki.okcore.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.item.ItemBlockOK;
import ruiseki.okcore.recipe.IOreDictEntry;
import ruiseki.okcore.registries.IRegistrable;

public interface IBlock extends IRegistrable<Block> {

    boolean isHasSubtypes();

    @Override
    default void register(String name) {
        get().setBlockName(name);
        registerBlock(name);
        registerTileEntity(name);
        registerComponent(name);
    }

    default void registerBlock(String name) {
        GameRegistry.registerBlock(this.get(), getItemBlockClass(), name);
    }

    default void registerTileEntity(String name) {}

    default void registerComponent(String name) {
        if (this instanceof IOreDictEntry oreDictEntry) oreDictEntry.registerOreDict();
        if (this instanceof IBlockPropertyProvider provider) provider.registerProperties();
    }

    default Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockOK.class;
    }
}

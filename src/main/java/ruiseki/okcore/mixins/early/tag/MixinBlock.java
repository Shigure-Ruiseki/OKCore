package ruiseki.okcore.mixins.early.tag;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.datastructure.IUniqueIndex;
import ruiseki.okcore.helper.TagHelpers;

@Mixin(Block.class)
public class MixinBlock implements IUniqueIndex {

    @Unique
    private static int okcore$NEXT_UNIQUE_ID = -1;
    @Unique
    private final int okcore$taggingHash = this.okcore$getNextID();

    @Unique
    private int okcore$getNextID() {
        TagHelpers.registerBlock(++okcore$NEXT_UNIQUE_ID, (Block) (Object) this);
        return okcore$NEXT_UNIQUE_ID;
    }

    @Override
    public int okcore$getUniqueID() {
        return this.okcore$taggingHash;
    }
}

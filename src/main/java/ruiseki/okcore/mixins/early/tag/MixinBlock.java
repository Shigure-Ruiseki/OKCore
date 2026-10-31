package ruiseki.okcore.mixins.early.tag;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.datastructure.IUniqueIndex;
import ruiseki.okcore.helper.TagHelpers;

@Mixin(Block.class)
@Implements({ @Interface(iface = IUniqueIndex.class, prefix = "okcoreid$") })
public class MixinBlock {

    @Unique
    private static int okcoreid$NEXT_UNIQUE_ID = -1;
    @Unique
    private final int okcoreid$taggingHash = this.okcore$getNextID();

    @Unique
    private int okcore$getNextID() {
        TagHelpers.registerBlock(++okcoreid$NEXT_UNIQUE_ID, (Block) (Object) this);
        return okcoreid$NEXT_UNIQUE_ID;
    }

    public int okcoreid$getUniqueID() {
        return okcoreid$taggingHash;
    }
}

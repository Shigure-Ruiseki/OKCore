package ruiseki.okcore.mixins.early.tag;

import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.datastructure.IUniqueIndex;
import ruiseki.okcore.helper.TagHelpers;

@Mixin(Item.class)
@Implements({ @Interface(iface = IUniqueIndex.class, prefix = "okcoreid$") })
public class MixinItem {

    @Unique
    private static int okcore$NEXT_UNIQUE_ID = -1;
    @Unique
    private final int hogutils$taggingHash = okcore$getNextID();

    @Unique
    private int okcore$getNextID() {
        TagHelpers.registerItem(++okcore$NEXT_UNIQUE_ID, (Item) (Object) this);
        return okcore$NEXT_UNIQUE_ID;
    }

    public int okcoreid$getUniqueID() {
        return hogutils$taggingHash;
    }
}

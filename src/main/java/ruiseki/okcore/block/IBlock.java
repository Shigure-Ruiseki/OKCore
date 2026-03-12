package ruiseki.okcore.block;

import net.minecraft.block.Block;

public interface IBlock {

    void init();

    Block getBlock();

    boolean isHasSubtypes();
}

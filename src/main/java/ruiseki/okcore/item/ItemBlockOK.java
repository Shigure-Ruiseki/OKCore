package ruiseki.okcore.item;

import net.minecraft.block.Block;

import ruiseki.okcore.block.IBlock;

@Deprecated
public class ItemBlockOK extends ItemBlockMetadata {

    public ItemBlockOK(Block block) {
        super(block);

        if (block instanceof IBlock iBlock) {
            this.hasSubtypes = iBlock.isHasSubtypes();
            this.setHasSubtypes(iBlock.isHasSubtypes());
        }
    }
}

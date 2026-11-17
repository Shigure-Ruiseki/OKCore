package ruiseki.okcore.modcompat.common.block;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

public class BlockFluidXpJuiceConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockFluidXpJuiceConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockFluidXpJuiceConfig() {
        super(OKCore._instance, true, "block_xpjuice", null, config -> new BlockFluidXpJuice());
    }

    @Override
    public boolean isDisableable() {
        return false;
    }
}

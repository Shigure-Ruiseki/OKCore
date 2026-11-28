package ruiseki.okcore.core.block;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

public class BlockBlockStateTestConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockBlockStateTestConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockBlockStateTestConfig() {
        super(OKCore._instance, true, "blockstate_test", null, itemConfig -> new BlockBlockStateTest());
    }

    @Override
    public void onRegistered() {
        super.onRegistered();


    }
}

package ruiseki.okcore.modcompat.common;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.Reference;
import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.enums.Mods;
import ruiseki.okcore.modcompat.IModCompat;
import ruiseki.okcore.modcompat.common.block.BlockFluidXpJuiceConfig;
import ruiseki.okcore.modcompat.common.fluid.FluidXpJuiceConfig;
import ruiseki.okcore.modcompat.common.item.ItemBucketXpJuiceConfig;

public class CommonModCompat implements IModCompat {

    @Override
    public String getModID() {
        return Reference.MOD_VANILLA;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getComment() {
        return "Utilities between mods";
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.PREINIT) {
            ConfigHandler configHandler = OKCore._instance.getConfigHandler();
            configHandler.add(new FluidXpJuiceConfig());
            configHandler.add(new BlockFluidXpJuiceConfig());
            configHandler.add(new ItemBucketXpJuiceConfig());
        }
    }
}

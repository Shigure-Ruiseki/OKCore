package ruiseki.commoncapabilities.modcompat.vanilla;

import net.minecraftforge.common.MinecraftForge;

import ruiseki.commoncapabilities.Reference;
import ruiseki.okcore.modcompat.IModCompat;

public class VanillaModCompat implements IModCompat {

    public VanillaModCompat() {
        MinecraftForge.EVENT_BUS.register(this);
    }

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
        return "Furnace and Brewing stand capabilities.";
    }

    @Override
    public void onInit(Step initStep) {

    }
}

package ruiseki.okcore.modcompat.baubles;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.inventory.RegistryInventoryLocation;
import ruiseki.okcore.modcompat.IModCompat;

public class BaubleModCompat implements IModCompat {

    /**
     * If the modcompat can be used.
     */
    public static boolean canBeUsed = false;

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.PREINIT) {
            canBeUsed = OKCore._instance.getModCompatLoader()
                .shouldLoadModCompat(this);

            if (canBeUsed) {
                RegistryInventoryLocation.getInstance()
                    .register(new InventoryLocationBaubles());
            }
        }
    }

    @Override
    public String getModID() {
        return "Baubles";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getComment() {
        return "Inventory iteration over baubles slots";
    }
}

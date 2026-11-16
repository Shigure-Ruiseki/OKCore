package ruiseki.okcore.modcompat.efr;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.enums.Mods;
import ruiseki.okcore.modcompat.IModCompat;
import ruiseki.okcore.modcompat.efr.recipe.type.cooking.blast.RecipeBlastFurnaceConfig;
import ruiseki.okcore.modcompat.efr.recipe.type.cooking.blast.RecipeTypeBlastFurnaceConfig;
import ruiseki.okcore.modcompat.efr.recipe.type.cooking.smoker.RecipeSmokerConfig;
import ruiseki.okcore.modcompat.efr.recipe.type.cooking.smoker.RecipeTypeSmokerConfig;

public class EFRModCompat implements IModCompat {

    /**
     * If the modcompat can be used.
     */
    public static boolean canBeUsed = false;

    @Override
    public String getModID() {
        return Mods.EtFuturum.getID();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getComment() {
        return "Integration with EtFuturum";
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.PREINIT) {
            canBeUsed = OKCore._instance.getModCompatLoader()
                .shouldLoadModCompat(this);

            ConfigHandler configHandler = OKCore._instance.getConfigHandler();
            configHandler.add(new RecipeTypeBlastFurnaceConfig());
            configHandler.add(new RecipeBlastFurnaceConfig());
            configHandler.add(new RecipeTypeSmokerConfig());
            configHandler.add(new RecipeSmokerConfig());

            EFRCookingHelpers.load();
        }
    }
}

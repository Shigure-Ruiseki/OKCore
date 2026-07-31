package ruiseki.okcore.modcompat.versionchecker;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.Reference;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.modcompat.IModCompat;
import ruiseki.okcore.tracking.IModVersion;

/**
 * Mod compat for the Version Checker mod.
 *
 * @author rubensworks
 *
 */
public class VersionCheckerModCompat implements IModCompat {

    private static boolean canBeUsed = false;

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.PREINIT) {
            canBeUsed = OKCore._instance.getModCompatLoader()
                .shouldLoadModCompat(this);
        }
    }

    @Override
    public String getModID() {
        return Reference.MOD_VERSION_CHECKER;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getComment() {
        return "Version Checker mod support.";
    }

    /**
     * Send a message to the Version Checker mod with the update info.
     * This is an integration with Dynious Version Checker See
     * http://www.minecraftforum.net/topic/2721902-
     *
     * @param mod        The mod.
     * @param modVersion The mod version holder.
     */
    public static synchronized void sendIMCOutdatedMessage(ModBase mod, IModVersion modVersion) {
        if (canBeUsed) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("modDisplayName", mod.getModName());
            compound.setString("oldVersion", mod.getReferenceValue(ModBase.REFKEY_MOD_VERSION));
            compound.setString("newVersion", modVersion.getVersion());

            compound.setString("updateUrl", modVersion.getUpdateUrl());
            compound.setBoolean("isDirectLink", true);
            compound.setString("changeLog", modVersion.getInfo());

            FMLInterModComms.sendRuntimeMessage(mod.getModId(), Reference.MOD_VERSION_CHECKER, "addUpdate", compound);
        }
    }

}

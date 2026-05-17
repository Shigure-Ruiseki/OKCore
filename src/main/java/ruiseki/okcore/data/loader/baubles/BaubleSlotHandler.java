package ruiseki.okcore.data.loader.baubles;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.lib.LibMods;

public class BaubleSlotHandler implements IInitListener {

    private static final List<BaubleSlotMaterial> materialsQueue = new ArrayList<>();

    public static void queueMaterial(BaubleSlotMaterial material) {
        if (material != null) {
            materialsQueue.add(material);
        }
    }

    @Override
    public void onInit(Step step) {
        if (step != Step.PREINIT) return;
        if (!LibMods.BaublesExpanded.isLoaded()) {
            materialsQueue.clear();
            return;
        }

        OKCore.okLog(
            Level.INFO,
            "[Baubles] Commencing batch registration for {} custom slot configs...",
            materialsQueue.size());
        for (BaubleSlotMaterial material : materialsQueue) {
            material.execute();
        }

        materialsQueue.clear();
        OKCore.okLog(Level.INFO, "[Baubles] Batch slot registration completed successfully.");
    }
}

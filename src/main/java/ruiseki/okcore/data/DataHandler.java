package ruiseki.okcore.data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.discovery.ASMDataTable;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.loader.DataLoader;
import ruiseki.okcore.data.loader.IDataLoader;

public class DataHandler {

    private static final Map<String, List<IDataLoader>> COMPONENTS = new HashMap<>();

    public static void register(IDataLoader component) {
        COMPONENTS.computeIfAbsent(component.getTargetFolder(), k -> new ArrayList<>())
            .add(component);
    }

    public static void handleMod(ResourceLocation id, String namespace, String folder, String[] subPaths,
        String fileName, InputStream inputStream) {
        List<IDataLoader> componentList = COMPONENTS.get(folder);

        if (componentList != null) {
            for (IDataLoader component : componentList) {
                if (component.isModLoader()) {
                    try {
                        component.process(id, namespace, folder, subPaths, fileName, inputStream);
                    } catch (Exception e) {
                        OKCore.okLog(
                            Level.ERROR,
                            "Mod Loader {} failed: {}",
                            component.getClass()
                                .getSimpleName(),
                            e.toString());
                    }
                }
            }
        }
    }

    public static void handleWorld(ResourceLocation id, String namespace, String folder, String[] subPaths,
        String fileName, InputStream inputStream) {
        List<IDataLoader> componentList = COMPONENTS.get(folder);
        if (componentList != null) {
            for (IDataLoader component : componentList) {
                if (component.isWorldLoader()) {
                    try {
                        component.process(id, namespace, folder, subPaths, fileName, inputStream);
                    } catch (Exception e) {
                        OKCore.okLog(
                            Level.ERROR,
                            "World Loader {} failed: {}",
                            component.getClass()
                                .getSimpleName(),
                            e.toString());
                    }
                }
            }
        }
    }

    public static void loadFromASM(ASMDataTable asmDataTable) {
        Set<ASMDataTable.ASMData> asmDataSet = asmDataTable.getAll(DataLoader.class.getName());

        for (ASMDataTable.ASMData asmData : asmDataSet) {
            try {
                Class<?> clazz = Class.forName(asmData.getClassName());

                if (IDataLoader.class.isAssignableFrom(clazz)) {
                    IDataLoader loader = (IDataLoader) clazz.getDeclaredConstructor()
                        .newInstance();
                    if (loader.shouldRegister()) {
                        register(loader);
                    } else {
                        OKCore.okLog(
                            Level.INFO,
                            "Skipping Data Loader [{}] because its required mod/condition is not met.",
                            loader.getClass()
                                .getSimpleName());
                    }
                }
            } catch (Exception e) {
                OKCore.okLog(
                    Level.ERROR,
                    "Failed to initialize ASM DataLoader [{}]: {}",
                    asmData.getClassName(),
                    e.toString());
            }
        }
    }
}

package ruiseki.okcore.mixins;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.falsepattern.deploader.DeploaderStub;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import ruiseki.okcore.config.ModConfig;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class EarlyMixinsLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {

    static {
        try {
            Class.forName("com.gtnewhorizons.gtnhextlib.core.GTNHExtLibCore", true, Launch.classLoader);
        } catch (ClassNotFoundException notExtLib) {
            removeBrigadierClassLoaderException();
            DeploaderStub.bootstrap(false);
            DeploaderStub.runDepLoader();
        }

        try {
            ModConfig.registerConfig();
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }

    private static void removeBrigadierClassLoaderException() {
        try {
            Field cleF = LaunchClassLoader.class.getDeclaredField("classLoaderExceptions");
            cleF.setAccessible(true);
            @SuppressWarnings("unchecked")
            Set<String> cle = (Set<String>) cleF.get(Launch.classLoader);
            // for Brigadier
            cle.remove("com.mojang.");
            // Thermos console log compat
            boolean hybridServer = Launch.classLoader.getResource("org/bukkit/World.class") != null
                || Launch.classLoader.getResource("thermos/Thermos.class") != null;
            if (hybridServer) {
                cle.add("com.mojang.util.QueueLogAppender");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public String getMixinConfig() {
        return "mixins.okcore.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return IMixins.getEarlyMixins(Mixins.class, loadedCoreMods);
    }
}

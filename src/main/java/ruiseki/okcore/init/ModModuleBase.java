package ruiseki.okcore.init;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import lombok.Getter;
import ruiseki.okcore.command.CommandMod;
import ruiseki.okcore.persist.world.WorldStorage;
import ruiseki.okcore.proxy.ICommonProxy;

public abstract class ModModuleBase {

    @Getter
    protected final ModBase mod;
    public final String moduleName;
    protected final ICommonProxy moduleProxy;
    private LiteralArgumentBuilder<ICommandSender> command;

    public ModModuleBase(ModBase mod, String moduleName) {
        this.mod = mod;
        this.moduleName = moduleName;
        this.moduleProxy = createProxy();
    }

    protected abstract ICommonProxy createProxy();

    public abstract boolean isEnable();

    protected void addInitListener(IInitListener listener) {
        mod.addInitListeners(listener);
    }

    protected void registerWorldStorage(WorldStorage storage) {
        mod.registerWorldStorage(storage);
    }

    protected LiteralArgumentBuilder<ICommandSender> constructModuleCommand(MinecraftServer server) {
        return new CommandMod(this.mod, this.moduleName).make();
    }

    /**
     * Reloads module's runtime data.
     * Called by /ok reload and module-specific reload commands.
     */
    public void reload(ICommandSender sender) throws Exception {}

    public ICommonProxy getModuleProxy() {
        return moduleProxy;
    }

    public abstract void preInit(FMLPreInitializationEvent event);

    public abstract void init(FMLInitializationEvent event);

    public abstract void postInit(FMLPostInitializationEvent event);

    public void onServerStarting(FMLServerStartingEvent event) {};

    public void onAboutToStartEvent(FMLServerAboutToStartEvent event) {};

    public void onServerStarted(FMLServerStartedEvent event) {};

    public void onServerStopping(FMLServerStoppingEvent event) {};

    public void onServerStopped(FMLServerStoppedEvent event) {};
}

package ruiseki.okcore.proxy;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.enums.Mods;
import ruiseki.okcore.event.handler.InputEventHandler;
import ruiseki.okcore.event.handler.TooltipEventHandler;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.tracking.Versions;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ClientProxyComponent {

    public ClientProxy() {
        super(new CommonProxy());
    }

    @Override
    public ModBase getMod() {
        return OKCore._instance;
    }

    @Override
    public void registerEventHooks() {
        super.registerEventHooks();
        MinecraftForge.EVENT_BUS.register(new Versions());

        MinecraftForge.EVENT_BUS.register(InputEventHandler.INSTANCE);
        if (Mods.MUI2.isModLoaded()) {
            MinecraftForge.EVENT_BUS.register(new TooltipEventHandler());
        }
    }
}

package ruiseki.okcore.proxy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.init.ModBase;

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
    }
}

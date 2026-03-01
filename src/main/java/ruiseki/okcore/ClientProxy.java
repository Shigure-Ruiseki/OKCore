package ruiseki.okcore;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.render.block.WorldRender;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.proxy.ClientProxyComponent;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ClientProxyComponent {

    public ClientProxy() {
        super(new CommonProxy());
    }

    @Override
    public ModBase getMod() {
        return OKCore.instance;
    }

    @Override
    public void registerRenderers() {
        super.registerRenderers();
        RenderingRegistry.registerBlockHandler(WorldRender.INSTANCE);
    }
}

package ruiseki.commoncapabilities.proxy;

import net.minecraftforge.common.MinecraftForge;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.okcore.audio.record.ClientRecordDataHandler;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.proxy.ClientProxyComponent;

/**
 * Proxy for the client side.
 *
 * @author rubensworks
 *
 */
public class ClientProxy extends ClientProxyComponent {

    public ClientProxy() {
        super(new CommonProxy());
    }

    @Override
    public ModBase getMod() {
        return CommonCapabilities._instance;
    }

    @Override
    public void registerEventHooks() {
        super.registerEventHooks();
        MinecraftForge.EVENT_BUS.register(ClientRecordDataHandler.INSTANCE);
    }
}

package ruiseki.commoncapabilities.proxy;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.proxy.CommonProxyComponent;

/**
 * Proxy for server and client side.
 * 
 * @author rubensworks
 *
 */
public class CommonProxy extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return CommonCapabilities._instance;
    }

}

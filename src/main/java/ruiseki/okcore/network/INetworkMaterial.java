package ruiseki.okcore.network;

import java.io.IOException;

public interface INetworkMaterial {

    void toNetwork(ExtendedBuffer buffer) throws IOException;

    void fromNetwork(ExtendedBuffer buffer) throws IOException;
}

package ruiseki.okcore.json;

import java.io.IOException;

import ruiseki.okcore.network.ExtendedBuffer;

public interface IJsonNetwork {

    void toNetwork(ExtendedBuffer buffer) throws IOException;

    void fromNetwork(ExtendedBuffer buffer) throws IOException;
}

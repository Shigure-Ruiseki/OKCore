package ruiseki.okcore.network;

import java.io.IOException;

public interface INetworkSerializer<T> {

    void toNetwork(ExtendedBuffer buffer, T value) throws IOException;

    T fromNetwork(ExtendedBuffer buffer) throws IOException;
}

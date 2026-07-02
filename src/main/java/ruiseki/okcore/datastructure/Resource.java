package ruiseki.okcore.datastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Resource {

    private final IoSupplier<InputStream> streamSupplier;

    public Resource(IoSupplier<InputStream> streamSupplier) {
        this.streamSupplier = streamSupplier;
    }

    public InputStream open() throws IOException {
        return this.streamSupplier.get();
    }

    public BufferedReader openAsReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.open(), StandardCharsets.UTF_8));
    }
}

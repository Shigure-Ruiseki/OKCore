package ruiseki.okcore.client.model.location;

import java.util.Objects;

import net.minecraft.util.ResourceLocation;

import com.gtnewhorizon.gtnhlib.client.model.loading.ResourceLoc;

import ruiseki.okcore.client.model.state.IStateModelMap;

public class StateLocation implements ResourceLoc<IStateModelMap> {

    private final String owner;
    private final String path;

    public StateLocation(ResourceLocation location) {
        this(location.getResourceDomain(), location.getResourcePath());
    }

    public StateLocation(String owner, String path) {
        this.owner = owner;
        this.path = path;
    }

    @Override
    public String prefix() {
        return "blockstates/";
    }

    @Override
    public String ext() {
        return ".json";
    }

    @Override
    public String owner() {
        return owner;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        StateLocation that = (StateLocation) obj;
        return Objects.equals(this.owner(), that.owner()) && Objects.equals(this.path(), that.path());
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, path);
    }

    @Override
    public Class<IStateModelMap> clazz() {
        return IStateModelMap.class;
    }
}

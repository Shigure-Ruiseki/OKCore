package ruiseki.okcore.tileentity;

import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Maps;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.event.OKEventFactory;

public class TileSideCapability extends TileEntityOK {

    private final Map<Pair<Capability<?>, ForgeDirection>, Object> capabilities = Maps.newHashMap();

    public TileSideCapability() {
        OKEventFactory.attachCapability(this);
    }

    public <T> void addCapabilityInternal(Capability<T> capability, T value) {
        capabilities.put(Pair.of(capability, null), value);
    }

    public <T> void addCapabilitySided(Capability<T> capability, ForgeDirection facing, T value) {
        capabilities.put(Pair.of(capability, facing), value);
    }

    protected Map<Pair<Capability<?>, ForgeDirection>, Object> getCapabilities() {
        return capabilities;
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, ForgeDirection facing) {
        return capabilities.containsKey(Pair.<Capability<?>, ForgeDirection>of(capability, facing))
            || (facing != null && capabilities.containsKey(Pair.<Capability<?>, ForgeDirection>of(capability, null)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@NotNull Capability<T> capability, ForgeDirection facing) {
        Object value = capabilities.get(Pair.<Capability<?>, ForgeDirection>of(capability, facing));
        if (value == null && facing != null) {
            value = capabilities.get(Pair.<Capability<?>, ForgeDirection>of(capability, null));
        }
        if (value != null) {
            return (T) value;
        }
        return null;
    }
}

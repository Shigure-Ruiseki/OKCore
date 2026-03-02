package ruiseki.okcore.energy.capability;

import org.jetbrains.annotations.Nullable;

public class WrappedEnergyIO implements IEnergyIO {

    @Nullable
    public final IEnergySource source;
    @Nullable
    public final IEnergySink sink;

    public WrappedEnergyIO(@Nullable IEnergySource source, @Nullable IEnergySink sink) {
        this.source = source;
        this.sink = sink;
    }

    @Override
    public int insert(int amount, boolean simulate) {
        return sink == null ? 0 : sink.insert(amount, simulate);
    }

    @Override
    public int extract(int amount, boolean simulate) {
        return source == null ? 0 : source.extract(amount, simulate);
    }

    @Override
    public boolean canConnect() {
        if ((sink != null && sink.canConnect()) || (source != null && source.canConnect())) {
            return true;
        }
        return false;
    }
}

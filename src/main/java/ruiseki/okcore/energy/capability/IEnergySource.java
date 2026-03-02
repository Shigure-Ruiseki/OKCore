package ruiseki.okcore.energy.capability;

public interface IEnergySource {

    int extract(int amount, boolean simulate);

    boolean canConnect();
}

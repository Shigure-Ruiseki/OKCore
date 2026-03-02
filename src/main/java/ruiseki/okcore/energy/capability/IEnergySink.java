package ruiseki.okcore.energy.capability;

public interface IEnergySink {

    int insert(int amount, boolean simulate);

    boolean canConnect();
}

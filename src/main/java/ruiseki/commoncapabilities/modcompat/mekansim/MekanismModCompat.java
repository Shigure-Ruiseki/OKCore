package ruiseki.commoncapabilities.modcompat.mekansim;

import net.minecraft.tileentity.TileEntity;

import mekanism.api.gas.IGasHandler;
import mekanism.common.tile.TileEntityFactory;
import mekanism.common.tile.TileEntityGasTank;
import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.commoncapabilities.modcompat.mekansim.capability.gashandler.GasHandlerConfig;
import ruiseki.commoncapabilities.modcompat.mekansim.capability.gashandler.MekanismGasHandler;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.modcompat.IModCompat;
import ruiseki.okcore.modcompat.capabilities.CapabilityConstructorRegistry;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.SimpleCapabilityConstructor;

public class MekanismModCompat implements IModCompat {

    @Override
    public String getModID() {
        return "Mekanism";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getComment() {
        return "Mekanims capabilities.";
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.INIT) {
            CapabilityConstructorRegistry registry = CommonCapabilities._instance.getCapabilityConstructorRegistry();
            // GasHandler
            registry
                .registerInheritableTile(IGasHandler.class, new SimpleCapabilityConstructor<IGasHandler, TileEntity>() {

                    @Override
                    public Capability<IGasHandler> getCapability() {
                        return GasHandlerConfig.CAPABILITY;
                    }

                    @Override
                    public ICapabilityProvider createProvider(TileEntity host) {
                        if (host instanceof TileEntityGasTank gasTankTile) {
                            return new DefaultCapabilityProvider<>(
                                this,
                                new MekanismGasHandler(host, t -> gasTankTile.gasTank));
                        }

                        if (host instanceof TileEntityFactory gasTankTile) {
                            return new DefaultCapabilityProvider<>(
                                this,
                                new MekanismGasHandler(host, t -> gasTankTile.gasTank));
                        }

                        if (host instanceof IGasHandler gasHandler) {
                            return new DefaultCapabilityProvider<>(this, new MekanismGasHandler(host, gasHandler));
                        }

                        return null;
                    }
                });
        }
    }
}

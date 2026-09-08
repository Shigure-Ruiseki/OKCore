package ruiseki.commoncapabilities.modcompat.mekansim;

import net.minecraft.tileentity.TileEntity;

import org.jetbrains.annotations.NotNull;

import mekanism.common.tile.TileEntityFactory;
import mekanism.common.tile.TileEntityGasTank;
import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.commoncapabilities.modcompat.mekansim.api.gas.IGasHandler;
import ruiseki.commoncapabilities.modcompat.mekansim.capability.gashandler.GasHandlerConfig;
import ruiseki.commoncapabilities.modcompat.mekansim.capability.gashandler.wrapper.GasHandlerWrapper;
import ruiseki.commoncapabilities.modcompat.mekansim.capability.gashandler.wrapper.GasTankWrapper;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.modcompat.IModCompat;
import ruiseki.okcore.modcompat.capabilities.CapabilityConstructorRegistry;
import ruiseki.okcore.modcompat.capabilities.DefaultSidedCapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.ICapabilityConstructor;

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
            registry.registerInheritableTile(
                mekanism.api.gas.IGasHandler.class,
                new ICapabilityConstructor<IGasHandler, TileEntity, TileEntity>() {

                    @Override
                    public Capability<IGasHandler> getCapability() {
                        return GasHandlerConfig.CAPABILITY;
                    }

                    @Override
                    public @NotNull ICapabilityProvider createProvider(TileEntity hostType, TileEntity host) {
                        if (host instanceof TileEntityGasTank gasTankTile) {
                            return new DefaultSidedCapabilityProvider<>(
                                DefaultSidedCapabilityProvider.forAllSides(
                                    getCapability(),
                                    side -> new GasTankWrapper(host, gasTankTile.gasTank, side)));
                        }

                        if (host instanceof TileEntityFactory gasTankTile) {
                            return new DefaultSidedCapabilityProvider<>(
                                DefaultSidedCapabilityProvider.forAllSides(
                                    getCapability(),
                                    side -> new GasTankWrapper(host, gasTankTile.gasTank, side)));
                        }

                        if (host instanceof mekanism.api.gas.IGasHandler gasHandler) {
                            return new DefaultSidedCapabilityProvider<>(
                                DefaultSidedCapabilityProvider.forAllSides(
                                    getCapability(),
                                    side -> new GasHandlerWrapper(host, gasHandler, side)));
                        }

                        return ICapabilityProvider.EMPTY;
                    }
                });
        }
    }
}

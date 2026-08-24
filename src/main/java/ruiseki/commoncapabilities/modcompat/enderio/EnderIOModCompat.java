package ruiseki.commoncapabilities.modcompat.enderio;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

import org.jetbrains.annotations.NotNull;

import cofh.api.energy.IEnergyStorage;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.enderio.power.IPowerStorage;
import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.commoncapabilities.modcompat.enderio.energystorage.EnderIOPowerWrapper;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.modcompat.IModCompat;
import ruiseki.okcore.modcompat.capabilities.CapabilityConstructorRegistry;
import ruiseki.okcore.modcompat.capabilities.DefaultSidedCapabilityProvider;
import ruiseki.okcore.modcompat.capabilities.ICapabilityConstructor;

public class EnderIOModCompat implements IModCompat {

    public EnderIOModCompat() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getModID() {
        return "EnderIO";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getComment() {
        return "EnderIO capabilities.";
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.INIT) {
            CapabilityConstructorRegistry registry = CommonCapabilities._instance.getCapabilityConstructorRegistry();

            // EnergyStorage
            registry.registerInheritableTile(
                IPowerStorage.class,
                new ICapabilityConstructor<IEnergyStorage, TileEntity, TileEntity>() {

                    @Override
                    public Capability<IEnergyStorage> getCapability() {
                        return CapabilityEnergy.ENERGY;
                    }

                    @Override
                    public @NotNull ICapabilityProvider createProvider(TileEntity hostType, TileEntity host) {
                        return new DefaultSidedCapabilityProvider<>(
                            DefaultSidedCapabilityProvider
                                .forAllSides(getCapability(), side -> new EnderIOPowerWrapper(host, side)));
                    }
                });
            registry.registerInheritableTile(
                IInternalPoweredTile.class,
                new ICapabilityConstructor<IEnergyStorage, TileEntity, TileEntity>() {

                    @Override
                    public Capability<IEnergyStorage> getCapability() {
                        return CapabilityEnergy.ENERGY;
                    }

                    @Override
                    public @NotNull ICapabilityProvider createProvider(TileEntity hostType, TileEntity host) {
                        return new DefaultSidedCapabilityProvider<>(
                            DefaultSidedCapabilityProvider
                                .forAllSides(getCapability(), side -> new EnderIOPowerWrapper(host, side)));
                    }
                });
        }
    }
}

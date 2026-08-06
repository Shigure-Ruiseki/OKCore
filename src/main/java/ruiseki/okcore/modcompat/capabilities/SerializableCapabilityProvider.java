package ruiseki.okcore.modcompat.capabilities;

import net.minecraft.nbt.NBTTagCompound;

import ruiseki.okcore.persist.nbt.INBTSerializable;

/**
 * A serializable implementation of the capability provider.
 *
 * @author rubensworks
 */
public abstract class SerializableCapabilityProvider<T> extends DefaultCapabilityProvider<T>
    implements INBTSerializable {

    public SerializableCapabilityProvider(ICapabilityTypeGetter<T> capabilityGetter, T capability) {
        super(capabilityGetter, capability);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return serializeNBT(capability);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        deserializeNBT(capability, nbt);
    }

    protected abstract NBTTagCompound serializeNBT(T capability);

    protected abstract void deserializeNBT(T capability, NBTTagCompound tag);
}

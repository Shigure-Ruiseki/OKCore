/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package ruiseki.okcore.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.persist.nbt.INBTSerializable;

@NotNullByDefault
public class CapabilityDispatcher implements INBTSerializable, ICapabilityProvider {

    private final ICapabilityProvider[] caps;
    private final INBTSerializable[] writers;
    private final String[] names;
    private final List<Runnable> listeners;

    public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list, List<Runnable> listeners) {
        this(list, listeners, null);
    }

    @SuppressWarnings("unchecked")
    public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list, List<Runnable> listeners,
        @Nullable ICapabilityProvider parent) {
        List<ICapabilityProvider> lstCaps = new ArrayList<>();
        List<INBTSerializable> lstWriters = new ArrayList<>();
        List<String> lstNames = new ArrayList<>();
        this.listeners = listeners;

        if (parent != null) // Parents go first!
        {
            lstCaps.add(parent);
            if (parent instanceof INBTSerializable) {
                lstWriters.add((INBTSerializable) parent);
                lstNames.add("Parent");
            }
        }

        for (Map.Entry<ResourceLocation, ICapabilityProvider> entry : list.entrySet()) {
            ICapabilityProvider prov = entry.getValue();
            lstCaps.add(prov);
            if (prov instanceof INBTSerializable) {
                lstWriters.add((INBTSerializable) prov);
                lstNames.add(
                    entry.getKey()
                        .toString());
            }
        }

        caps = lstCaps.toArray(new ICapabilityProvider[lstCaps.size()]);
        writers = lstWriters.toArray(new INBTSerializable[lstWriters.size()]);
        names = lstNames.toArray(new String[lstNames.size()]);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable ForgeDirection side) {
        for (ICapabilityProvider c : caps) {
            LazyOptional<T> ret = c.getCapability(cap, side);
            // noinspection ConstantConditions
            if (ret == null) {
                throw new RuntimeException(
                    String.format(
                        Locale.ENGLISH,
                        "Provider %s.getCapability() returned null; return LazyOptional.empty() instead!",
                        c.getClass()
                            .getTypeName()));
            }
            if (ret.isPresent()) {
                return ret;
            }
        }
        return LazyOptional.empty();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        for (int x = 0; x < writers.length; x++) {
            nbt.setTag(names[x], writers[x].serializeNBT());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for (int x = 0; x < writers.length; x++) {
            if (nbt.hasKey(names[x])) {
                writers[x].deserializeNBT(nbt.getCompoundTag(names[x]));
            }
        }
    }

    public boolean areCompatible(@Nullable CapabilityDispatcher other) // Called from ItemStack to compare equality.
    { // Only compares serializeable caps.
        if (other == null) return this.writers.length == 0; // Done this way so we can do some pre-checks before doing
                                                            // the costly NBT serialization and compare
        if (this.writers.length == 0) return other.writers.length == 0;
        return this.serializeNBT()
            .equals(other.serializeNBT());
    }

    public void invalidate() {
        this.listeners.forEach(Runnable::run);
    }
}

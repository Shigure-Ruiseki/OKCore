package ruiseki.okcore.inventory;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.init.IRegistry;

/**
 * @author rubensworks
 */
public interface IRegistryInventoryLocation extends IRegistry {

    public void register(IInventoryLocation inventoryLocation);

    @Nullable
    public IInventoryLocation get(ResourceLocation uniqueName);

    public Collection<IInventoryLocation> values();

}

package ruiseki.okcore.item.capability.wrapper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.inventory.EntityEquipmentSlot;
import ruiseki.okcore.item.handler.IItemHandler;

/**
 * Exposes the armor inventory of an {@link EntityLivingBase} as an {@link IItemHandler} using
 * {@link EntityLivingBase#getEquipmentInSlot(int)} and
 * {@link EntityLivingBase#setCurrentItemOrArmor(int, ItemStack)}.
 */
public class EntityArmorInvWrapper extends EntityEquipmentInvWrapper {

    public EntityArmorInvWrapper(final EntityLivingBase entity) {
        super(entity, EntityEquipmentSlot.Type.ARMOR);
    }
}

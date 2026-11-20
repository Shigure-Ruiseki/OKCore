package ruiseki.okcore.item.capability.wrapper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.inventory.EntityEquipmentSlot;
import ruiseki.okcore.item.handler.IItemHandler;

/**
 * Exposes the hands inventory of an {@link EntityLivingBase} as an {@link IItemHandler} using
 * {@link EntityLivingBase#getEquipmentInSlot(int)} and
 * {@link EntityLivingBase#setCurrentItemOrArmor(int, ItemStack)}.
 */
public class EntityHandsInvWrapper extends EntityEquipmentInvWrapper {

    public EntityHandsInvWrapper(EntityLivingBase entity) {
        super(entity, EntityEquipmentSlot.Type.HAND);
    }
}

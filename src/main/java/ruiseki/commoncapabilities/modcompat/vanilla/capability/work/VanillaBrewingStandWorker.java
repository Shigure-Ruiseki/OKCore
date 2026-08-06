package ruiseki.commoncapabilities.modcompat.vanilla.capability.work;

import java.util.Objects;

import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;

import ruiseki.commoncapabilities.api.capability.work.IWorker;

/**
 * Worker capability for the vanilla brewing stand tile entity
 *
 * @author rubensworks
 */
public class VanillaBrewingStandWorker implements IWorker {

    private static final int[] OUTPUT_SLOTS = new int[] { 0, 1, 2 };
    private static final int INGREDIENT_SLOT = 3;

    private final TileEntityBrewingStand brewingStand;

    public VanillaBrewingStandWorker(TileEntityBrewingStand brewingStand) {
        this.brewingStand = brewingStand;
    }

    @Override
    public boolean hasWork() {
        ItemStack ingredient = brewingStand.getStackInSlot(INGREDIENT_SLOT);
        if (ingredient == null || ingredient.stackSize <= 0) {
            return false;
        }

        if (!Objects.requireNonNull(ingredient.getItem())
            .isPotionIngredient(ingredient)) {
            return false;
        }

        for (int slot : OUTPUT_SLOTS) {
            ItemStack potion = brewingStand.getStackInSlot(slot);
            if (potion != null && potion.getItem() instanceof ItemPotion) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canWork() {
        return brewingStand.getBrewTime() > 0 || hasWork();
    }
}

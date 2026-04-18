package ruiseki.okcore.helper;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionHelpers {

    public static ItemStack createPotion(int meta) {
        return new ItemStack(Items.potionitem, 1, meta);
    }

    public static ItemStack createPotion(Potion potion) {
        return createPotion(potion.getId());
    }

    public static List<PotionEffect> getEffects(ItemStack stack) {
        if (stack == null || stack.getItem() != Items.potionitem) return null;
        return Items.potionitem.getEffects(stack);
    }

    public static boolean hasEffect(ItemStack stack, Potion potion) {
        List<PotionEffect> effects = getEffects(stack);
        if (effects == null) return false;

        for (PotionEffect effect : effects) {
            if (effect.getPotionID() == potion.id) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasEffect(ItemStack stack, Potion potion, int amplifier) {
        List<PotionEffect> effects = getEffects(stack);
        if (effects == null) return false;

        for (PotionEffect effect : effects) {
            if (effect.getPotionID() == potion.id && effect.getAmplifier() == amplifier) {
                return true;
            }
        }
        return false;
    }
}

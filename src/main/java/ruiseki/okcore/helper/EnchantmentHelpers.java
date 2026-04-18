package ruiseki.okcore.helper;

import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EnchantmentHelpers {

    public static ItemStack createEnchantment(int meta) {
        return new ItemStack(Items.enchanted_book, 1, meta);
    }

    public static ItemStack createEnchantment(Enchantment effect) {
        return createEnchantment(effect.effectId);
    }

    public static Map<Integer, Integer> getEnchantments(ItemStack stack) {
        if (stack == null) return null;
        return EnchantmentHelper.getEnchantments(stack);
    }

    public static boolean hasEnchantment(ItemStack stack, Enchantment ench) {
        if (stack == null) return false;
        return getLevel(stack, ench) > 0;
    }

    public static boolean hasEnchantment(ItemStack stack, Enchantment ench, int level) {
        return getLevel(stack, ench) >= level;
    }

    public static int getLevel(ItemStack stack, Enchantment ench) {
        if (stack == null) return 0;
        return EnchantmentHelper.getEnchantmentLevel(ench.effectId, stack);
    }

    public static void addEnchantment(ItemStack stack, Enchantment ench, int level) {
        if (stack == null) return;
        stack.addEnchantment(ench, level);
    }

    public static void removeEnchantment(ItemStack stack, Enchantment ench) {
        if (stack == null) return;
        Map<Integer, Integer> enchants = getEnchantments(stack);
        enchants.remove(ench.effectId);
        EnchantmentHelper.setEnchantments(enchants, stack);
    }
}

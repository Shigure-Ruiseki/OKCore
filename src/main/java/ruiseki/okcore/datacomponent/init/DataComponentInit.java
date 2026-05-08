package ruiseki.okcore.datacomponent.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.datacomponent.components.BooleanComponent;
import ruiseki.okcore.datacomponent.components.IntegerComponent;
import ruiseki.okcore.datacomponent.components.StringComponent;
import ruiseki.okcore.datacomponent.registry.DataComponentRegistry;
import ruiseki.okcore.helper.EntityHelpers;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.item.cooldown.IItemCooldown;

public class DataComponentInit implements IInitListener {

    @Override
    public void onInit(Step step) {
        if (step != Step.PREINIT) return;

        DataComponentRegistry.registerComponent(new DamagedComponent());
        DataComponentRegistry.registerComponent(new BrokenComponent());
        DataComponentRegistry.registerComponent(new CarriedComponent());
        DataComponentRegistry.registerComponent(new ExtendedViewComponent());
        DataComponentRegistry.registerComponent(new FishingRodCastComponent());
        DataComponentRegistry.registerComponent(new UsingItemComponent());
        DataComponentRegistry.registerComponent(new RarityComponent());
        DataComponentRegistry.registerComponent(new DamageComponent());
        DataComponentRegistry.registerComponent(new MaxDamageComponent());
        DataComponentRegistry.registerComponent(new StackSizeComponent());
        DataComponentRegistry.registerComponent(new MaxStackSizeComponent());
        DataComponentRegistry.registerComponent(new RepairableComponent());
        DataComponentRegistry.registerComponent(new RepairCostComponent());
        DataComponentRegistry.registerComponent(new UnbreakableComponent());
        DataComponentRegistry.registerComponent(new CooldownComponent());

    }

    private static class DamagedComponent implements BooleanComponent {

        @Override
        public String getName() {
            return "damaged";
        }

        @Override
        public Boolean getValue(ItemStack stack) {
            return stack.isItemDamaged();
        }
    }

    private static class BrokenComponent implements BooleanComponent {

        @Override
        public String getName() {
            return "broken";
        }

        @Override
        public Boolean getValue(ItemStack stack) {
            if (stack == null) return false;
            Item item = stack.getItem();
            if (item == null || !item.isDamageable()) return false;
            int maxDamage = stack.getMaxDamage();
            int currentDamage = stack.getItemDamage();
            return currentDamage >= (maxDamage - 1);
        }
    }

    private static class CarriedComponent implements BooleanComponent {

        @Override
        public String getName() {
            return "carried";
        }

        @Override
        public Boolean getValue(ItemStack stack) {
            if (stack == null) return false;
            GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
            if (currentScreen instanceof GuiContainer) {
                ItemStack mouseStack = Minecraft.getMinecraft().thePlayer.inventory.getItemStack();
                return mouseStack == stack;
            }

            return false;
        }
    }

    private static class ExtendedViewComponent implements BooleanComponent {

        @Override
        public String getName() {
            return "extended_view";
        }

        @Override
        public Boolean getValue(ItemStack stack) {
            if (Minecraft.getMinecraft().currentScreen == null) {
                return false;
            }
            return GuiScreen.isShiftKeyDown();
        }
    }

    private static class FishingRodCastComponent implements BooleanComponent {

        @Override
        public String getName() {
            return "fishing_rod/cast";
        }

        @Override
        public Boolean getValue(ItemStack stack) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player == null) return false;

            return player.inventory.getCurrentItem() == stack && player.fishEntity != null;
        }
    }

    private static class UsingItemComponent implements BooleanComponent {

        @Override
        public String getName() {
            return "using_item";
        }

        @Override
        public Boolean getValue(ItemStack stack) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player == null) return false;
            return player.isUsingItem() && player.getItemInUse() == stack;
        }
    }

    private static class DamageComponent implements IntegerComponent {

        @Override
        public String getName() {
            return "damage";
        }

        @Override
        public Integer getValue(ItemStack stack) {
            return stack.getItemDamage();
        }
    }

    private static class MaxDamageComponent implements IntegerComponent {

        @Override
        public String getName() {
            return "max_damage";
        }

        @Override
        public Integer getValue(ItemStack stack) {
            return stack.getMaxDamage();
        }
    }

    private static class StackSizeComponent implements IntegerComponent {

        @Override
        public String getName() {
            return "stack_size";
        }

        @Override
        public Integer getValue(ItemStack stack) {
            return stack.stackSize;
        }
    }

    private static class MaxStackSizeComponent implements IntegerComponent {

        @Override
        public String getName() {
            return "max_stack_size";
        }

        @Override
        public Integer getValue(ItemStack stack) {
            return stack.getMaxStackSize();
        }
    }

    private static class RarityComponent implements StringComponent {

        @Override
        public String getName() {
            return "rarity";
        }

        @Override
        public String getValue(ItemStack stack) {
            return stack.getRarity().rarityName;
        }
    }

    private static class RepairCostComponent implements IntegerComponent {

        @Override
        public String getName() {
            return "repair_cost";
        }

        @Override
        public Integer getValue(ItemStack stack) {
            return stack.getRepairCost();
        }
    }

    private static class RepairableComponent implements BooleanComponent {

        @Override
        public String getName() {
            return "repairable";
        }

        @Override
        public Boolean getValue(ItemStack stack) {
            if (stack == null || stack.getItem() == null) {
                return false;
            }
            return stack.getItem()
                .isRepairable();
        }
    }

    private static class UnbreakableComponent implements BooleanComponent {

        @Override
        public String getName() {
            return "unbreakable";
        }

        @Override
        public Boolean getValue(ItemStack stack) {
            if (stack == null || !stack.hasTagCompound()) {
                return false;
            }
            return stack.stackTagCompound.getBoolean("Unbreakable");
        }
    }

    private static class CooldownComponent implements IntegerComponent {

        @Override
        public String getName() {
            return "cooldown";
        }

        @Override
        public Integer getValue(ItemStack stack) {
            if (!(stack.getItem() instanceof IItemCooldown)) return 0;
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            return EntityHelpers.getCooldownTracker(player)
                .getCooldown(stack.getItem());
        }
    }

}

package ruiseki.okcore.datacomponent.init;

import java.lang.reflect.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.datacomponent.component.UseCooldown;
import ruiseki.okcore.datacomponent.componenttype.BooleanComponent;
import ruiseki.okcore.datacomponent.componenttype.IntegerComponent;
import ruiseki.okcore.datacomponent.componenttype.StringComponent;
import ruiseki.okcore.datacomponent.core.DataComponentType;
import ruiseki.okcore.datacomponent.registry.DataComponentRegistry;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.item.cooldown.IItemCooldown;

public class DataComponents implements IInitListener {

    @Override
    public void onInit(Step step) {
        if (step != Step.PREINIT) return;

        DataComponentRegistry.registerComponent(new DamagedComponent());
        DataComponentRegistry.registerComponent(new BrokenComponent());
        DataComponentRegistry.registerComponent(new CarriedComponent());
        DataComponentRegistry.registerComponent(new ExtendedViewComponent());
        DataComponentRegistry.registerComponent(new RarityComponent());
        DataComponentRegistry.registerComponent(new DamageComponent());
        DataComponentRegistry.registerComponent(new MaxDamageComponent());
        DataComponentRegistry.registerComponent(new StackSizeComponent());
        DataComponentRegistry.registerComponent(new MaxStackSizeComponent());
        DataComponentRegistry.registerComponent(new RepairableComponent());
        DataComponentRegistry.registerComponent(new RepairCostComponent());
        DataComponentRegistry.registerComponent(new UnbreakableComponent());

        DataComponentRegistry.registerComponent(USE_COOLDOWN);
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

    public static final DataComponentType<UseCooldown> USE_COOLDOWN = new DataComponentType<>() {

        @Override
        public String getName() {
            return "use_cooldown";
        }

        @Override
        public Type getType() {
            return UseCooldown.class;
        }

        @Override
        public boolean appliesTo(ItemStack stack, Item item, int meta) {
            return item instanceof IItemCooldown;
        }

        @Override
        public UseCooldown getValue(ItemStack stack) {
            if (stack == null || !(stack.getItem() instanceof IItemCooldown cooldown)) {
                return null;
            }
            return cooldown.getUseCooldown(stack);
        }
    };
}

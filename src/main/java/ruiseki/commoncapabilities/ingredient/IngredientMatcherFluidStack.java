package ruiseki.commoncapabilities.ingredient;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.commoncapabilities.api.capability.fluidhandler.FluidMatch;
import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.helper.Helpers;

/**
 * Matcher for FluidStacks.
 *
 * @author rubensworks
 */
public class IngredientMatcherFluidStack implements IIngredientMatcher<FluidStack, Integer> {

    @Override
    public boolean isInstance(Object object) {
        return object instanceof FluidStack;
    }

    @Override
    public Integer getAnyMatchCondition() {
        return FluidMatch.ANY;
    }

    @Override
    public Integer getExactMatchCondition() {
        return FluidMatch.EXACT;
    }

    @Override
    public Integer getExactMatchNoQuantityCondition() {
        return FluidMatch.FLUID | FluidMatch.NBT;
    }

    @Override
    public Integer withCondition(Integer matchCondition, Integer with) {
        return matchCondition | with;
    }

    @Override
    public Integer withoutCondition(Integer matchCondition, Integer without) {
        return matchCondition & ~without;
    }

    @Override
    public boolean hasCondition(Integer matchCondition, Integer searchCondition) {
        return (matchCondition & searchCondition) > 0;
    }

    @Override
    public boolean matches(FluidStack a, FluidStack b, Integer matchCondition) {
        return FluidMatch.areFluidStacksEqual(a, b, matchCondition);
    }

    @Override
    public FluidStack getEmptyInstance() {
        return FluidHelpers.EMPTY;
    }

    @Override
    public int hash(FluidStack instance) {
        if (FluidHelpers.isEmpty(instance)) {
            return 0;
        }

        int code = 1;
        code = 31 * code + (instance.getFluid() != null ? instance.getFluid()
            .hashCode() : 0);
        code = 31 * code + instance.amount;
        if (instance.tag != null) {
            code = 31 * code + instance.tag.hashCode();
        }
        return code;
    }

    @Override
    public FluidStack copy(FluidStack instance) {
        if (FluidHelpers.isEmpty(instance)) {
            return getEmptyInstance();
        }
        return instance.copy();
    }

    @Override
    public long getQuantity(FluidStack instance) {
        return instance != null ? instance.amount : 0;
    }

    @Override
    public FluidStack withQuantity(FluidStack instance, long quantity) {
        if (quantity == 0) {
            return getEmptyInstance();
        }
        if (FluidHelpers.isEmpty(instance)) {
            return new FluidStack(FluidRegistry.WATER, Helpers.castSafe(quantity));
        }
        if (instance.amount == quantity) {
            return instance;
        }
        FluidStack copy = instance.copy();
        copy.amount = Helpers.castSafe(quantity);
        return copy;
    }

    @Override
    public long getMaximumQuantity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int conditionCompare(Integer a, Integer b) {
        return Integer.compare(a, b);
    }

    @Override
    public String localize(FluidStack instance) {
        if (FluidHelpers.isEmpty(instance)) {
            return "";
        }
        return instance.getLocalizedName();
    }

    @Override
    public String toString(FluidStack instance) {
        if (FluidHelpers.isEmpty(instance)) {
            return "EMPTY";
        }
        String fluidName = instance.getFluid() != null ? instance.getFluid()
            .getName() : "null";
        return String.format("%s %d %s", fluidName, instance.amount, instance.tag);
    }

    @Override
    public int compare(FluidStack o1, FluidStack o2) {
        boolean empty1 = FluidHelpers.isEmpty(o1);
        boolean empty2 = FluidHelpers.isEmpty(o2);

        if (empty1) {
            return empty2 ? 0 : -1;
        } else if (empty2) {
            return 1;
        }

        if (o1.getFluid() == o2.getFluid()) {
            if (o1.amount == o2.amount) {
                return IngredientHelpers.compareTags(o1.tag, o2.tag);
            }
            return Integer.compare(o1.amount, o2.amount);
        }

        String name1 = o1.getFluid() != null ? o1.getFluid()
            .getName() : "";
        String name2 = o2.getFluid() != null ? o2.getFluid()
            .getName() : "";
        return name1.compareTo(name2);
    }

}

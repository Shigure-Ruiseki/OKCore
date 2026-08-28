package ruiseki.commoncapabilities.ingredient;

import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Exact matcher for a void match condition.
 *
 * @author rubensworks
 */
public class IngredientMatcherEnergy implements IIngredientMatcher<Long, Boolean> {

    @Override
    public boolean isInstance(Object object) {
        return object instanceof Long;
    }

    @Override
    public Boolean getAnyMatchCondition() {
        return false;
    }

    @Override
    public Boolean getExactMatchCondition() {
        return true;
    }

    @Override
    public Boolean getExactMatchNoQuantityCondition() {
        return false;
    }

    @Override
    public Boolean withCondition(Boolean matchCondition, Boolean with) {
        return matchCondition || with;
    }

    @Override
    public Boolean withoutCondition(Boolean matchCondition, Boolean without) {
        return matchCondition == without ? false : matchCondition;
    }

    @Override
    public boolean hasCondition(Boolean matchCondition, Boolean searchCondition) {
        return matchCondition == searchCondition;
    }

    @Override
    public boolean matches(Long a, Long b, Boolean matchCondition) {
        return !matchCondition || (a != null && b != null && a.longValue() == b.longValue());
    }

    @Override
    public Long getEmptyInstance() {
        return 0L;
    }

    @Override
    public boolean isEmpty(Long instance) {
        return instance == null || instance == 0L;
    }

    @Override
    public int hash(Long instance) {
        return instance == null ? 0 : Long.hashCode(instance);
    }

    @Override
    public Long copy(Long instance) {
        return instance;
    }

    @Override
    public long getQuantity(Long instance) {
        return instance == null ? 0L : instance;
    }

    @Override
    public Long withQuantity(Long instance, long quantity) {
        return quantity;
    }

    @Override
    public long getMaximumQuantity() {
        return Long.MAX_VALUE;
    }

    @Override
    public int conditionCompare(Boolean a, Boolean b) {
        return (a ? 1 : 0) - (b ? 1 : 0);
    }

    @Override
    public String localize(Long instance) {
        return LangHelpers.localize("recipecomponent.minecraft.energy");
    }

    @Override
    public int compare(Long o1, Long o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;
        return Long.compare(o1, o2);
    }
}

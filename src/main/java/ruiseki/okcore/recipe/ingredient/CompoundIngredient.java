package ruiseki.okcore.recipe.ingredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import ruiseki.okcore.network.ExtendedBuffer;

/** Ingredient that matches if any of the child ingredients match */
public class CompoundIngredient extends AbstractIngredient {

    private List<Ingredient> children;
    private ItemStack[] stacks;
    private IntList itemIds;
    private final boolean isSimple;

    public CompoundIngredient(List<Ingredient> children) {
        super(Stream.of());
        this.children = Collections.unmodifiableList(children);
        this.isSimple = children.stream()
            .allMatch(Ingredient::isSimple);
    }

    /**
     * Creates a compound ingredient from the given list of ingredients
     */
    public static Ingredient of(Ingredient... children) {
        // if 0 or 1 ingredient, can save effort
        if (children.length == 0) throw new IllegalArgumentException(
            "Cannot create a compound ingredient with no children, use Ingredient.of() to create an empty ingredient");
        if (children.length == 1) return children[0];

        // need to merge vanilla ingredients, as otherwise the JSON produced by this ingredient could be invalid
        List<Ingredient> vanillaIngredients = new ArrayList<>();
        List<Ingredient> allIngredients = new ArrayList<>();
        for (Ingredient child : children) {
            if (child.getSerializer() == VanillaIngredientSerializer.INSTANCE) vanillaIngredients.add(child);
            else allIngredients.add(child);
        }
        if (!vanillaIngredients.isEmpty()) allIngredients.add(merge(vanillaIngredients));
        if (allIngredients.size() == 1) return allIngredients.get(0);
        return new CompoundIngredient(allIngredients);
    }

    @Override
    @Nonnull
    public ItemStack[] getItems() {
        // An empty result is recomputed rather than cached, because a child may only be unresolved and become
        // available once the entries it refers to are registered.
        if (stacks == null || stacks.length == 0) {
            List<ItemStack> tmp = Lists.newArrayList();
            for (Ingredient child : children) Collections.addAll(tmp, child.getItems());
            ItemStack[] resolved = tmp.toArray(new ItemStack[tmp.size()]);

            // Only a non empty result is cached, so a compound whose children resolve later is not stuck empty.
            stacks = resolved.length > 0 ? resolved : null;

            return resolved;
        }

        return stacks;
    }

    @Override
    @NotNull
    public IntList getStackingIds() {
        boolean childrenNeedInvalidation = false;
        for (Ingredient child : children) {
            childrenNeedInvalidation |= child.checkInvalidation();
        }

        // An empty result is recomputed rather than cached, matching getItems, so a compound whose children
        // resolve later is not stuck without ids.
        if (childrenNeedInvalidation || this.itemIds == null || this.itemIds.isEmpty() || checkInvalidation()) {
            this.markValid();
            IntList ids = new IntArrayList();
            for (Ingredient child : children) ids.addAll(child.getStackingIds());
            ids.sort(IntComparators.NATURAL_COMPARATOR);

            this.itemIds = ids.isEmpty() ? null : ids;

            return ids;
        }

        return this.itemIds;
    }

    @Override
    public boolean test(@Nullable ItemStack target) {
        if (target == null) return false;

        return children.stream()
            .anyMatch(c -> c.test(target));
    }

    @Override
    protected void invalidate() {
        this.itemIds = null;
        this.stacks = null;
    }

    @Override
    public boolean isSimple() {
        return isSimple;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Nonnull
    public Collection<Ingredient> getChildren() {
        return this.children;
    }

    @Override
    public JsonElement toJson() {
        if (this.children.size() == 1) {
            return this.children.getFirst()
                .toJson();
        } else {
            JsonArray json = new JsonArray();
            this.children.forEach(e -> json.add(e.toJson()));
            return json;
        }
    }

    public static class Serializer implements IIngredientSerializer<CompoundIngredient> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public CompoundIngredient fromNetwork(ExtendedBuffer buffer) {
            return new CompoundIngredient(
                Stream.generate(() -> Ingredient.fromNetwork(buffer))
                    .limit(buffer.readVarIntFromBuffer())
                    .collect(Collectors.toList()));
        }

        @Override
        public CompoundIngredient fromJson(JsonObject json) {
            throw new JsonSyntaxException(
                "CompoundIngredient should not be directly referenced in json, just use an array of ingredients.");
        }

        @Override
        public void toNetwork(ExtendedBuffer buffer, CompoundIngredient ingredient) {
            buffer.writeVarIntToBuffer(ingredient.children.size());
            ingredient.children.forEach(c -> c.toNetwork(buffer));
        }
    }
}

package ruiseki.okcore.recipe.ingredient;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

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

    @Override
    @Nonnull
    public ItemStack[] getItems() {
        if (stacks == null) {
            List<ItemStack> tmp = Lists.newArrayList();
            for (Ingredient child : children) Collections.addAll(tmp, child.getItems());
            stacks = tmp.toArray(new ItemStack[tmp.size()]);

        }
        return stacks;
    }

    @Override
    @Nonnull
    public IntList getStackingIds() {
        // TODO: Add a child.isInvalid()?
        if (this.itemIds == null) {
            this.itemIds = new IntArrayList();
            for (Ingredient child : children) this.itemIds.addAll(child.getStackingIds());
            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
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
        // Shouldn't need to invalidate children as this is only called form invalidateAll..
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

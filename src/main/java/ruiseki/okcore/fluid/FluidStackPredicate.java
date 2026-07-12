package ruiseki.okcore.fluid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A predicate for FluidStacks.
 */
@SuppressWarnings("unused")
@FunctionalInterface
public interface FluidStackPredicate extends Predicate<FluidStack> {

    FluidStackPredicate ALL = stack -> true;

    @Override
    boolean test(FluidStack stack);

    @Nullable
    default Collection<FluidStack> getFluids() {
        return null;
    }

    default @NotNull FluidStackPredicate and(FluidStackPredicate other) {
        Objects.requireNonNull(other);

        return new FluidStackPredicate() {

            private List<FluidStack> fluids;

            @Override
            public boolean test(FluidStack t) {
                return FluidStackPredicate.this.test(t) && other.test(t);
            }

            @Override
            public @Nullable Collection<FluidStack> getFluids() {
                Collection<FluidStack> a = FluidStackPredicate.this.getFluids();
                Collection<FluidStack> b = other.getFluids();

                if (a == null && b == null) return null;
                if (a == null || b == null) return a == null ? b : a;

                if (this.fluids != null) return this.fluids;

                this.fluids = new ArrayList<>(a.size() + b.size());
                this.fluids.addAll(a);
                this.fluids.addAll(b);

                return this.fluids;
            }
        };
    }

    default @NotNull FluidStackPredicate negate() {
        return t -> t != null && !FluidStackPredicate.this.test(t);
    }

    default @NotNull FluidStackPredicate or(FluidStackPredicate other) {
        Objects.requireNonNull(other);

        return new FluidStackPredicate() {

            @Override
            public boolean test(FluidStack t) {
                return FluidStackPredicate.this.test(t) || other.test(t);
            }

            @Override
            public @Nullable Collection<FluidStack> getFluids() {
                Collection<FluidStack> a = FluidStackPredicate.this.getFluids();
                Collection<FluidStack> b = other.getFluids();

                if (a == null && b == null) return null;
                if (a == null || b == null) return a == null ? b : a;

                ArrayList<FluidStack> fluids = new ArrayList<>(a.size() + b.size());
                fluids.addAll(a);
                fluids.addAll(b);

                return fluids;
            }
        };
    }

    default @NotNull FluidStackPredicate withAmount(int amount) {
        return withAmount(amount, amount);
    }

    default @NotNull FluidStackPredicate withAmount(int min, int max) {
        return new FluidStackPredicate() {

            @Override
            public boolean test(FluidStack stack) {
                if (stack == null || stack.amount < min || stack.amount > max) return false;
                return FluidStackPredicate.this.test(stack);
            }

            @Override
            public @Nullable Collection<FluidStack> getFluids() {
                return FluidStackPredicate.this.getFluids();
            }
        };
    }

    static @NotNull FluidStackPredicate not(FluidStackPredicate target) {
        Objects.requireNonNull(target);
        return target.negate();
    }

    static @NotNull FluidStackPredicate and(FluidStackPredicate a, FluidStackPredicate b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return a.and(b);
    }

    static FluidStackPredicate matches(FluidStack test) {
        if (test == null || test.getFluid() == null) return ALL;

        return new FluidStackPredicate() {

            private List<FluidStack> list;

            @Override
            public boolean test(FluidStack stack) {
                if (stack == null) return false;
                return stack.isFluidEqual(test);
            }

            @Override
            public List<FluidStack> getFluids() {
                if (list == null) list = Collections.singletonList(test);
                return list;
            }
        };
    }

    static FluidStackPredicate amount(int amount) {
        return stack -> stack != null && stack.amount == amount;
    }

    static FluidStackPredicate amountRange(int lower, int upper) {
        return stack -> stack != null && stack.amount >= lower && stack.amount <= upper;
    }
}

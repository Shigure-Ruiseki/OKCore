package ruiseki.okcore.registries;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.tag.ResourceKey;

@Deprecated
public final class RegistryObject<T> implements Supplier<T> {

    @Nullable
    private final ResourceLocation name;
    @Nullable
    private final ResourceKey<T> key;
    @Nullable
    private T value;

    private static final RegistryObject<?> EMPTY = new RegistryObject<>();

    public static <T, U extends T> RegistryObject<U> create(final ResourceKey<U> key) {
        return new RegistryObject<>(key);
    }

    public static <T, U extends T> RegistryObject<U> create(final ResourceLocation name,
        final ResourceKey<T> registryKey) {
        return new RegistryObject<>(name, registryKey.location());
    }

    @SuppressWarnings("unchecked")
    public static <T> RegistryObject<T> empty() {
        return (RegistryObject<T>) EMPTY;
    }

    private RegistryObject() {
        this.name = null;
        this.key = null;
        this.value = null;
    }

    public RegistryObject(ResourceKey<T> key) {
        this.key = Objects.requireNonNull(key, "ResourceKey cannot be null");
        this.name = key.location();
    }

    private RegistryObject(ResourceLocation name, ResourceLocation registryName) {
        this.name = Objects.requireNonNull(name, "ResourceLocation name cannot be null");
        this.key = ResourceKey.create(ResourceKey.createRegistryKey(registryName), name);
    }

    public void updateReference(T value) {
        this.value = value;
    }

    @NotNull
    @Override
    public T get() {
        T ret = this.value;
        Objects.requireNonNull(ret, () -> "Registry Object not present: " + (this.name != null ? this.name : "null"));
        return ret;
    }

    @Nullable
    public ResourceLocation getId() {
        return this.name;
    }

    @Nullable
    public ResourceKey<T> getKey() {
        return this.key;
    }

    public boolean isPresent() {
        return this.value != null;
    }

    public Stream<T> stream() {
        return isPresent() ? Stream.of(get()) : Stream.empty();
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (isPresent()) consumer.accept(get());
    }

    public RegistryObject<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent()) return this;
        return predicate.test(get()) ? this : empty();
    }

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return !isPresent() ? Optional.empty() : Optional.ofNullable(mapper.apply(get()));
    }

    public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
        Objects.requireNonNull(mapper);
        return !isPresent() ? Optional.empty() : Objects.requireNonNull(mapper.apply(get()));
    }

    public <U> Supplier<U> lazyMap(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return () -> isPresent() ? mapper.apply(get()) : null;
    }

    public T orElse(T other) {
        return isPresent() ? get() : other;
    }

    public T orElseGet(Supplier<? extends T> other) {
        return isPresent() ? get() : other.get();
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isPresent()) return get();
        throw exceptionSupplier.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof RegistryObject<?>other && Objects.equals(other.name, this.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}

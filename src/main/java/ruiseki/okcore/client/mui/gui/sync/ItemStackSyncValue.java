package ruiseki.okcore.client.mui.gui.sync;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.value.sync.ValueSyncHandler;

import ruiseki.okcore.helper.ItemHelpers;

public class ItemStackSyncValue extends ValueSyncHandler<ItemStack, ItemStackSyncValue> {

    private ItemStack cache;
    private final Supplier<ItemStack> getter;
    private final Consumer<ItemStack> setter;

    public ItemStackSyncValue(@NotNull Supplier<ItemStack> getter, @Nullable Consumer<ItemStack> setter) {
        this.getter = Objects.requireNonNull(getter);
        this.setter = setter;
        this.cache = getter.get();
    }

    public ItemStackSyncValue(@NotNull Supplier<ItemStack> getter) {
        this(getter, (Consumer<ItemStack>) null);
    }

    @Contract("null, null -> fail")
    public ItemStackSyncValue(@Nullable Supplier<ItemStack> clientGetter, @Nullable Supplier<ItemStack> serverGetter) {
        this(clientGetter, null, serverGetter, null);
    }

    @Contract("null, _, null, _ -> fail")
    public ItemStackSyncValue(@Nullable Supplier<ItemStack> clientGetter, @Nullable Consumer<ItemStack> clientSetter,
        @Nullable Supplier<ItemStack> serverGetter, @Nullable Consumer<ItemStack> serverSetter) {
        if (clientGetter == null && serverGetter == null) {
            throw new NullPointerException("Client or server getter must not be null!");
        }
        if (NetworkUtils.isClient()) {
            this.getter = clientGetter != null ? clientGetter : serverGetter;
            this.setter = clientSetter != null ? clientSetter : serverSetter;
        } else {
            this.getter = serverGetter != null ? serverGetter : clientGetter;
            this.setter = serverSetter != null ? serverSetter : clientSetter;
        }
        this.cache = this.getter.get();
    }

    @Override
    public ItemStack getValue() {
        return this.cache;
    }

    @Override
    public void setValue(ItemStack value, boolean setSource, boolean sync) {
        this.cache = value;
        if (setSource && this.setter != null) {
            this.setter.accept(value);
        }
        onValueChanged();
        if (sync) sync();
    }

    @Override
    public boolean updateCacheFromSource(boolean isFirstSync) {
        if (isFirstSync || !ItemHelpers.areItemsEqual(this.getter.get(), cache)) {
            this.setValue(this.getter.get(), false, false);
            return true;
        }
        return false;
    }

    @Override
    public void notifyUpdate() {
        this.setValue(this.getter.get(), false, true);
    }

    @Override
    public void write(PacketBuffer buffer) throws IOException {
        ItemStack value = this.getter.get();
        buffer.writeBoolean(!ItemHelpers.isEmpty(value));
        if (value != null) {
            NetworkUtils.writeItemStack(buffer, value);
        }
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        ItemStack value = buffer.readBoolean() ? NetworkUtils.readItemStack(buffer) : ItemHelpers.EMPTY;
        this.setValue(value, false, false);
    }

    @Override
    public Class<ItemStack> getValueType() {
        return ItemStack.class;
    }
}

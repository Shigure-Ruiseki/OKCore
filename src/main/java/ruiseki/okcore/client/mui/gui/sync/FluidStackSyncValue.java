package ruiseki.okcore.client.mui.gui.sync;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.network.NetworkUtils;
import com.cleanroommc.modularui.value.sync.ValueSyncHandler;

import ruiseki.okcore.helper.FluidHelpers;

public class FluidStackSyncValue extends ValueSyncHandler<FluidStack, FluidStackSyncValue> {

    private FluidStack cache;
    private final Supplier<FluidStack> getter;
    private final Consumer<FluidStack> setter;

    public FluidStackSyncValue(@NotNull Supplier<FluidStack> getter, @Nullable Consumer<FluidStack> setter) {
        this.getter = Objects.requireNonNull(getter);
        this.setter = setter;
        this.cache = getter.get();
    }

    public FluidStackSyncValue(@NotNull Supplier<FluidStack> getter) {
        this(getter, (Consumer<FluidStack>) null);
    }

    @Contract("null, null -> fail")
    public FluidStackSyncValue(@Nullable Supplier<FluidStack> clientGetter,
        @Nullable Supplier<FluidStack> serverGetter) {
        this(clientGetter, null, serverGetter, null);
    }

    @Contract("null, _, null, _ -> fail")
    public FluidStackSyncValue(@Nullable Supplier<FluidStack> clientGetter, @Nullable Consumer<FluidStack> clientSetter,
        @Nullable Supplier<FluidStack> serverGetter, @Nullable Consumer<FluidStack> serverSetter) {
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
    public FluidStack getValue() {
        return this.cache;
    }

    @Override
    public void setValue(FluidStack value, boolean setSource, boolean sync) {
        this.cache = value;
        if (setSource && this.setter != null) {
            this.setter.accept(value);
        }
        onValueChanged();
        if (sync) sync();
    }

    @Override
    public boolean updateCacheFromSource(boolean isFirstSync) {
        if (isFirstSync || !FluidHelpers.areFluidStacksEqual(this.getter.get(), cache)) {
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
        FluidStack value = this.getter.get();
        buffer.writeBoolean(!FluidHelpers.isEmpty(value));
        if (value != null) {
            NetworkUtils.writeFluidStack(buffer, value);
        }
    }

    @Override
    public void read(PacketBuffer buffer) throws IOException {
        FluidStack value = buffer.readBoolean() ? NetworkUtils.readFluidStack(buffer) : FluidHelpers.EMPTY;
        this.setValue(value, false, false);
    }

    @Override
    public Class<FluidStack> getValueType() {
        return FluidStack.class;
    }
}

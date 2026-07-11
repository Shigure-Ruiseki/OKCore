package ruiseki.okcore.core;

import java.util.function.Supplier;

import net.minecraft.block.Block;

import ruiseki.okcore.Reference;
import ruiseki.okcore.block.IBlock;
import ruiseki.okcore.registries.DeferredRegister;
import ruiseki.okcore.registries.RegistryObject;
import ruiseki.okcore.tag.Registries;

public final class OKCoreBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Reference.MOD_ID);

    private static RegistryObject<Block> register(String name, Supplier<Boolean> configCondition,
        Supplier<IBlock> blockSupplier) {
        if (!configCondition.get()) {
            return RegistryObject.empty();
        }

        return BLOCKS.register(
            name,
            () -> {
                return blockSupplier.get()
                    .getBlock();
            });
    }

    public static void register() {
        BLOCKS.register();
    }

    private OKCoreBlocks() {}
}

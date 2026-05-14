package ruiseki.okcore.guide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import ruiseki.okcore.guide.impl.Book;

public interface IGuideBook {

    /**
     * Build your guide book here. The returned book will be registered for you. The book created here can be modified
     * later, so make sure to keep a reference for yourself.
     *
     * @return a built book to be registered.
     */
    @Nullable
    Book buildBook();

    /**
     * An IRecipe to use for your book. Called from {@link cpw.mods.fml.common.registry.GameRegistry<IRecipe>}
     *
     * @return an IRecipe to register for your book or null to not include one.
     */
    @Nullable
    default IRecipe getRecipe(@Nonnull ItemStack bookStack) {
        return null;
    }

    /**
     * Called during Post Initialization.
     */
    default void handlePost(@Nonnull ItemStack bookStack) {
        // No-op
    }

    default boolean shouldRegister() {
        return true;
    }
}

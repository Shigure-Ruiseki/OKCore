package ruiseki.okcore.network.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.google.common.collect.Lists;

import ruiseki.okcore.event.recipes.RecipesUpdatedEvent;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.recipe.RecipeRegistry;

public class PacketUpdateRecipes extends PacketCodec {

    private List<IRecipeOK<?>> recipes = Lists.newArrayList();

    public PacketUpdateRecipes() {}

    public PacketUpdateRecipes(Collection<IRecipeOK<?>> recipes) {
        this.recipes = Lists.newArrayList(recipes);
    }

    @Override
    public void decode(ExtendedBuffer input) {
        this.recipes = new ArrayList<>();
        int count = input.readVarIntFromBuffer();
        for (int i = 0; i < count; i++) {
            try {
                IRecipeOK<?> recipe = fromNetwork(input);
                if (recipe != null) {
                    this.recipes.add(recipe);
                }
            } catch (IOException ignored) {}
        }
    }

    @Override
    public void encode(ExtendedBuffer output) {
        output.writeVarIntToBuffer(this.recipes.size());
        for (IRecipeOK<?> recipe : this.recipes) {
            toNetwork(output, recipe);
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        RecipeManager.getManager()
            .replaceRecipes(this.recipes);
        RecipeRegistry.syncMCCraftingManager();
        RecipeRegistry.syncMCFurnaceRecipes();
        MinecraftForge.EVENT_BUS.post(new RecipesUpdatedEvent(RecipeManager.getManager()));
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {}

    public static IRecipeOK<?> fromNetwork(ExtendedBuffer buffer) throws IOException {
        ResourceLocation serializerKey = buffer.readResourceLocation();
        ResourceLocation recipeId = buffer.readResourceLocation();

        IRecipeSerializer<?> serializer = RecipeRegistry.getSerializer(serializerKey);
        if (serializer == null) {
            throw new IllegalArgumentException("Unknown recipe serializer " + serializerKey);
        }

        return serializer.fromNetwork(recipeId, buffer);
    }

    @SuppressWarnings({ "unchecked" })
    public static <T extends IRecipeOK<?>> void toNetwork(ExtendedBuffer buffer, T recipe) {
        IRecipeSerializer<T> serializer = (IRecipeSerializer<T>) recipe.getSerializer();
        ResourceLocation serializerKey = RecipeRegistry.getKey(serializer);

        if (serializerKey == null) {
            throw new IllegalArgumentException(
                "Recipe serializer is not registered: " + serializer.getClass()
                    .getName());
        }

        buffer.writeResourceLocation(serializerKey);
        buffer.writeResourceLocation(recipe.getId());
        try {
            serializer.toNetwork(buffer, recipe);
        } catch (IOException ignore) {}
    }
}

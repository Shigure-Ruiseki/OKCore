package ruiseki.okcore.network.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.recipe.RecipeRegistry;

public class PacketUpdateRecipes extends PacketCodec {

    private List<IRecipeOK<?>> recipes;

    public PacketUpdateRecipes() {
        this.recipes = Lists.newArrayList();
    }

    public PacketUpdateRecipes(Collection<IRecipeOK<?>> recipes) {
        this.recipes = Lists.newArrayList(recipes);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void decode(ExtendedBuffer input) {
        this.recipes = new ArrayList<>();
        try {
            int count = input.readVarIntFromBuffer();
            for (int i = 0; i < count; i++) {
                IRecipeSerializer serializer = RecipeRegistry.getSerializer(input.readString());
                ResourceLocation id = new ResourceLocation(input.readString());
                if (serializer != null) {
                    IRecipeOK<?> decoded = serializer.fromNetwork(id, input);
                    if (decoded != null) this.recipes.add(decoded);
                }
            }
        } catch (IOException e) {
            OKCore.okLog(Level.ERROR, "Failed to decode recipes", e);
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void encode(ExtendedBuffer output) {
        try {
            output.writeVarIntToBuffer(this.recipes.size());
            for (IRecipeOK<?> iRecipe : this.recipes) {
                IRecipeSerializer serializer = iRecipe.getSerializer();
                output.writeString(serializer.getTypeKey());
                output.writeString(
                    iRecipe.getId()
                        .toString());
                serializer.toNetwork(output, iRecipe);
            }
        } catch (IOException e) {
            OKCore.okLog(Level.ERROR, "Failed to encode recipes", e);
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
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {}
}

package ruiseki.commoncapabilities.api.capability.recipehandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;

/**
 * Defines the inputs and outputs of a recipe.
 * Inputs are ingredient prototypes for ingredient component types.
 * Outputs are exact instances for ingredient component types.
 *
 * Implementing classes should properly implement the equals and hashCode methods.
 *
 * @author rubensworks
 */
public interface IRecipeDefinition extends Comparable<IRecipeDefinition> {

    /**
     * @return The input ingredient component types.
     */
    public Set<IngredientComponent<?, ?>> getInputComponents();

    /**
     * Get the input prototypes of a certain type.
     *
     * The first list contains a list of ingredients,
     * whereas the deeper second list contains different prototype-based alternatives for the ingredient at this
     * position.
     *
     * @param ingredientComponent An ingredient component type.
     * @param <T>                 The instance type.
     * @param <M>                 The matching condition parameter, may be Void.
     * @return Input prototypes.
     */
    public <T, M> List<IPrototypedIngredientAlternatives<T, M>> getInputs(
        IngredientComponent<T, M> ingredientComponent);

    /**
     * @return The output ingredients.
     */
    public IMixedIngredients getOutput();

    /**
     * Serialize a recipe to NBT using NBTTagList for inputs.
     *
     * @param recipe A recipe.
     * @return An NBT representation of the given recipe.
     */
    @SuppressWarnings("unchecked")
    public static NBTTagCompound serialize(IRecipeDefinition recipe) {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList inputList = new NBTTagList();

        for (IngredientComponent<?, ?> component : recipe.getInputComponents()) {
            NBTTagCompound componentEntry = new NBTTagCompound();

            componentEntry.setString(
                "component",
                component.getName()
                    .toString());
            NBTTagList instances = new NBTTagList();
            for (IPrototypedIngredientAlternatives ingredient : recipe.getInputs(component)) {
                NBTTagCompound subTag = new NBTTagCompound();
                IPrototypedIngredientAlternatives.ISerializer serializer = ingredient.getSerializer();
                subTag.setTag("val", serializer.serialize(component, ingredient));
                subTag.setByte("type", serializer.getId());
                instances.appendTag(subTag);
            }

            componentEntry.setTag("instances", instances);
            inputList.appendTag(componentEntry);
        }

        tag.setTag("input", inputList);
        tag.setTag("output", IMixedIngredients.serialize(recipe.getOutput()));
        return tag;
    }

    /**
     * Deserialize a recipe from NBT using NBTTagList.
     *
     * @param tag An NBT tag.
     * @return A new mixed recipe instance.
     * @throws IllegalArgumentException If the given tag is invalid or does not contain data on the given recipe.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static RecipeDefinition deserialize(NBTTagCompound tag) throws IllegalArgumentException {
        Map<IngredientComponent<?, ?>, List<IPrototypedIngredientAlternatives<?, ?>>> inputs = new HashMap<>();

        if (!tag.hasKey("input")) {
            throw new IllegalArgumentException("A recipe tag did not contain a valid input tag");
        }
        if (!tag.hasKey("output")) {
            throw new IllegalArgumentException("A recipe tag did not contain a valid output tag");
        }

        NBTBase inputRawTag = tag.getTag("input");

        if (inputRawTag instanceof NBTTagList inputList) {
            for (Object entryObj : inputList.tagList) {
                if (!(entryObj instanceof NBTTagCompound componentEntry)) continue;
                String componentName = componentEntry.getString("component");
                IngredientComponent<?, ?> component = IngredientComponent.REGISTRY
                    .get(new ResourceLocation(componentName));
                if (component == null) {
                    throw new IllegalArgumentException("Could not find the ingredient component type " + componentName);
                }

                NBTTagList instancesTag = (NBTTagList) componentEntry.getTag("instances");
                List<IPrototypedIngredientAlternatives<?, ?>> instances = Lists.newArrayList();
                if (instancesTag != null) {
                    for (Object instanceTag : instancesTag.tagList) {
                        IPrototypedIngredientAlternatives.ISerializer alternativeSerializer;
                        NBTBase deserializeTag;
                        if (instanceTag instanceof NBTTagList) {
                            alternativeSerializer = PrototypedIngredientAlternativesList.SERIALIZER;
                            deserializeTag = (NBTBase) instanceTag;
                        } else if (instanceTag instanceof NBTTagCompound instanceTagCompound) {
                            byte type = instanceTagCompound.getByte("type");
                            alternativeSerializer = IPrototypedIngredientAlternatives.SERIALIZERS.get(type);
                            if (alternativeSerializer == null) {
                                throw new IllegalArgumentException(
                                    "Could not find a prototyped ingredient alternative serializer for id " + type);
                            }
                            deserializeTag = instanceTagCompound.getTag("val");
                        } else {
                            throw new IllegalArgumentException(
                                "The ingredient component type " + componentName
                                    + " did not contain a valid reference to instances");
                        }
                        IPrototypedIngredientAlternatives alternatives = alternativeSerializer
                            .deserialize(component, deserializeTag);
                        instances.add(alternatives);
                    }
                }
                inputs.put(component, instances);
            }
        }
        IMixedIngredients output = IMixedIngredients.deserialize(tag.getCompoundTag("output"));
        return new RecipeDefinition(inputs, output);
    }

}

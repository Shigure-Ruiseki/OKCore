package ruiseki.commoncapabilities.api.capability.recipehandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
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
     * If the input at the given index is reusable.
     * If an ingredient is reusable, this means that a crafting job for this recipe will not (fully) consume this
     * ingredient, and could potentially be reused in later crafting jobs.
     *
     * @param ingredientComponent An ingredient component type.
     * @param index               The index of an input, based on the order in {@link #getInputs(IngredientComponent)}.
     * @param <T>                 The instance type.
     * @param <M>                 The matching condition parameter, may be Void.
     * @return If the input at this index is reusable.
     */
    public <T, M> boolean isInputReusable(IngredientComponent<T, M> ingredientComponent, int index);

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
        NBTTagList inputReusableList = new NBTTagList();

        for (IngredientComponent<?, ?> component : recipe.getInputComponents()) {
            if (component == null || component.getRegistryName() == null) continue;

            String componentName = component.getRegistryName()
                .toString();

            // 1. Serialize inputs
            NBTTagCompound componentEntry = new NBTTagCompound();
            componentEntry.setString("component", componentName);

            NBTTagList instances = new NBTTagList();
            List<Byte> reusableBytes = Lists.newArrayList();
            int index = 0;

            for (IPrototypedIngredientAlternatives ingredient : recipe.getInputs(component)) {
                NBTTagCompound subTag = new NBTTagCompound();
                IPrototypedIngredientAlternatives.ISerializer serializer = ingredient.getSerializer();
                subTag.setTag("val", serializer.serialize(component, ingredient));
                subTag.setByte("type", serializer.getId());
                instances.appendTag(subTag);

                reusableBytes.add((byte) (recipe.isInputReusable(component, index) ? 1 : 0));
                index++;
            }

            componentEntry.setTag("instances", instances);
            inputList.appendTag(componentEntry);

            // 2. Serialize inputReusable bằng NBTTagList đồng bộ cấu trúc với inputList
            NBTTagCompound reusableEntry = new NBTTagCompound();
            reusableEntry.setString("component", componentName);

            byte[] byteArray = new byte[reusableBytes.size()];
            for (int i = 0; i < reusableBytes.size(); i++) {
                byteArray[i] = reusableBytes.get(i);
            }
            reusableEntry.setTag("reusable", new NBTTagByteArray(byteArray));
            inputReusableList.appendTag(reusableEntry);
        }

        tag.setTag("input", inputList);
        tag.setTag("inputReusable", inputReusableList);
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
        Map<IngredientComponent<?, ?>, List<Boolean>> inputsReusable = new HashMap<>();

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
                    .getValue(new ResourceLocation(componentName));
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
        NBTBase rawReusableTag = tag.getTag("inputReusable");
        if (rawReusableTag instanceof NBTTagList inputReusableList) {
            for (Object entryObj : inputReusableList.tagList) {
                if (!(entryObj instanceof NBTTagCompound reusableEntry)) continue;
                String componentName = reusableEntry.getString("component");
                IngredientComponent<?, ?> component = IngredientComponent.REGISTRY
                    .getValue(new ResourceLocation(componentName));
                if (component == null) {
                    throw new IllegalArgumentException("Could not find the ingredient component type " + componentName);
                }

                NBTBase subTag = reusableEntry.getTag("reusable");
                if (subTag instanceof NBTTagByteArray instancesReusable) {
                    List<Boolean> inputReusable = Lists.newArrayList();
                    for (byte b : instancesReusable.func_150292_c()) {
                        inputReusable.add(b == (byte) 1);
                    }
                    inputsReusable.put(component, inputReusable);
                }
            }
        }

        IMixedIngredients output = IMixedIngredients.deserialize(tag.getCompoundTag("output"));
        return new RecipeDefinition(inputs, inputsReusable, output);
    }

}

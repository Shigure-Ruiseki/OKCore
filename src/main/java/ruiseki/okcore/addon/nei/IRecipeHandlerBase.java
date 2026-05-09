package ruiseki.okcore.addon.nei;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;

public interface IRecipeHandlerBase extends ICraftingHandler, IUsageHandler {

    void prepare();
}

package ruiseki.okcore.event.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.recipe.type.crafting.ICraftingRecipe;

public class RecipeRemainingItemsHandler {

    public static final RecipeRemainingItemsHandler INSTANCE = new RecipeRemainingItemsHandler();

    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.craftMatrix == null || event.player == null) return;

        IInventory matrix = event.craftMatrix;
        if (!(matrix instanceof InventoryCrafting inv)) return;

        EntityPlayer player = event.player;

        ICraftingRecipe matchedRecipe = null;
        for (Object r : CraftingManager.getInstance()
            .getRecipeList()) {
            if (r instanceof ICraftingRecipe recipe && recipe.matchesOK(inv, player.worldObj)) {
                matchedRecipe = recipe;
                break;
            }
        }

        if (matchedRecipe != null) {
            NonNullList<ItemStack> remainingItems = matchedRecipe.getRemainingItems(inv);

            for (int i = 0; i < remainingItems.size(); i++) {
                ItemStack remaining = remainingItems.get(i);
                ItemStack slotStack = matrix.getStackInSlot(i);

                // Vanilla will handle it
                if (slotStack != null && slotStack.getItem() != null
                    && slotStack.getItem()
                        .hasContainerItem(slotStack)) {
                    continue;
                }

                if (!player.inventory.addItemStackToInventory(remaining)) {
                    if (!player.worldObj.isRemote) {
                        player.dropPlayerItemWithRandomChoice(remaining, false);
                    }
                }
            }
        }
    }
}

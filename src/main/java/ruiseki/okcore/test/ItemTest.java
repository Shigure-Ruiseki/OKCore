package ruiseki.okcore.test;

import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import ruiseki.okcore.datacomponent.component.UseCooldown;
import ruiseki.okcore.item.IItemCooldown;
import ruiseki.okcore.item.IItemToggle;
import ruiseki.okcore.item.ItemOK;

public class ItemTest extends ItemOK implements IItemCooldown, IItemToggle {

    public ItemTest() {
        super("item_test");
        setTextureName("stick");
    }

    public static void register() {
        ItemTest test = new ItemTest();
        test.init();
    }

    @Override
    public UseCooldown getUseCooldown(ItemStack stack) {
        return new UseCooldown(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World worldIn, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }

        worldIn.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        if (!worldIn.isRemote) {
            worldIn.spawnEntityInWorld(new EntityEnderPearl(worldIn, player));
        }

        return stack;
    }

    @Override
    public void toggle(EntityPlayer player, ItemStack held) {
        boolean wasOn = isOn(held);
        boolean newState = !wasOn;

        setOn(held, newState);

        if (!player.worldObj.isRemote) {
            String statusColor = newState ? "§a" : "§c";
            String statusText = newState ? "ON" : "OFF";

            player.addChatComponentMessage(
                new ChatComponentText("§7[" + held.getDisplayName() + "§7] " + statusColor + statusText));
        }
    }
}

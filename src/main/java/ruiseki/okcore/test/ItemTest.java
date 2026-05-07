package ruiseki.okcore.test;

import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.okcore.helper.EntityHelpers;
import ruiseki.okcore.item.ItemOK;
import ruiseki.okcore.item.cooldown.IItemCooldown;

public class ItemTest extends ItemOK implements IItemCooldown {

    public ItemTest() {
        super("item_test");
        setTextureName("stick");
    }

    public static void register() {
        ItemTest test = new ItemTest();
        test.init();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            --itemStackIn.stackSize;
        }

        worldIn.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        EntityHelpers.getCooldownTracker(player)
            .setCooldown(this, 20);

        if (!worldIn.isRemote) {
            worldIn.spawnEntityInWorld(new EntityEnderPearl(worldIn, player));
        }

        return itemStackIn;
    }
}

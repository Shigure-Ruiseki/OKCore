package ruiseki.okcore.test;

import java.util.List;
import java.util.Set;

import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.datacomponent.component.UseCooldown;
import ruiseki.okcore.helper.TileHelpers;
import ruiseki.okcore.item.IItemCooldown;
import ruiseki.okcore.item.IItemToggle;
import ruiseki.okcore.item.ItemOK;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;
import ruiseki.okcore.tag.entry.TagEntry;

public class ItemItemTest extends ItemOK implements IItemCooldown, IItemToggle {

    public ItemItemTest() {
        super("item_test");
        setTextureName("stick");
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
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null) {
            ForgeDirection direction = ForgeDirection.getOrientation(side);

            return TileHelpers.getCapability(te, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction)
                .map(handler -> {
                    ItemStack toInsert = new ItemStack(Items.stick);
                    ItemStack remainder = toInsert;

                    for (int i = 0; i < handler.getSlots(); i++) {
                        remainder = handler.insertItem(i, remainder, false);
                        if (remainder == null || remainder.stackSize <= 0) {
                            break;
                        }
                    }

                    if (remainder == null || remainder.stackSize < toInsert.stackSize) {
                        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.pop", 0.5F, 1.0F);
                        return true;
                    } else {
                        player.addChatComponentMessage(new ChatComponentText("§cTile Entity full!"));
                    }
                    return false;
                })
                .orElse(false);
        }
        return false;
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

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        TagKey<ItemStack> dustTagKey = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "rods"));

        Set<TagEntry<ItemStack>> entries = TagManager.getManager()
            .getEntries(dustTagKey);

        list.add("§6Items in #forge:rods:");
        if (entries.isEmpty()) {
            list.add(" §7(Empty Tag)");
        } else {
            for (TagEntry<ItemStack> entry : entries) {
                String itemId = entry.getId()
                    .toString();
                int meta = entry.getMeta();
                list.add(" §7- " + itemId + (meta == TagEntry.WILDCARD ? ":*" : ":" + meta));
            }
        }
    }
}

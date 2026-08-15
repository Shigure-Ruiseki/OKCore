package ruiseki.okcore.core;

import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.config.configurable.ConfigurableItem;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.item.IItemCooldown;
import ruiseki.okcore.item.IItemToggle;
import ruiseki.okcore.item.UseCooldown;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.TagEntry;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;

public class ItemInventoryTest extends ConfigurableItem implements IItemCooldown, IItemToggle {

    private static ItemInventoryTest _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static ItemInventoryTest getInstance() {
        return _instance;
    }

    public ItemInventoryTest(ItemConfig eConfig) {
        super(eConfig);
        setTextureName("stick");
    }

    @Override
    public UseCooldown getUseCooldown(ItemStack stack) {
        return new UseCooldown(1);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null) {
            ForgeDirection direction = ForgeDirection.getOrientation(side);

            return CapabilityHelpers.getCapability(te, CapabilityItemHandler.ITEM_HANDLER, direction)
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
        return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
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
        super.addInformation(stack, player, list, flag);
        TagKey<Item> dustTagKey = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "rods"));

        Set<TagEntry> entries = TagManager.getManager()
            .getEntries(dustTagKey);

        list.add("§6Items in #forge:rods:");
        if (entries.isEmpty()) {
            list.add(" §7(Empty Tag)");
        } else {
            for (TagEntry entry : entries) {
                String itemId = entry.id()
                    .toString();
                int meta = entry.meta();
                list.add(" §7- " + itemId + (meta == TagEntry.WILDCARD ? ":*" : ":" + meta));
            }
        }
    }
}

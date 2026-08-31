package ruiseki.okcore.item;

import java.util.Optional;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.helper.CooldownHelpers;
import ruiseki.okcore.helper.ItemHelpers;

public class UseCooldown {

    private float seconds;
    private Optional<ResourceLocation> group;

    public UseCooldown() {}

    public UseCooldown(float seconds) {
        this(seconds, Optional.empty());
    }

    public UseCooldown(float seconds, Optional<ResourceLocation> group) {
        this.seconds = seconds;
        this.group = group;
    }

    public int getTicks() {
        return (int) (seconds * 20);
    }

    public void apply(ItemStack stack, EntityPlayer player) {
        if (player == null || ItemHelpers.isEmpty(stack)) return;
        CooldownHelpers.addCooldown(stack, player, this.getTicks());
    }

    public Optional<ResourceLocation> cooldownGroup() {
        return group;
    }
}

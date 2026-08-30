package ruiseki.okcore.entity.cooldown;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import ruiseki.okcore.item.IItemCooldown;
import ruiseki.okcore.item.UseCooldown;

public class ItemCooldowns {

    private final Map<ResourceLocation, CooldownInstance> cooldowns = Maps.newHashMap();
    private int tickCount;

    public ItemCooldowns() {}

    public boolean isOnCooldown(ItemStack item) {
        return this.getCooldownPercent(item, 0.0F) > 0.0F;
    }

    public float getCooldownPercent(ItemStack item, float a) {
        ResourceLocation group = this.getCooldownGroup(item);
        if (group == null) return 0.0F;
        CooldownInstance cooldown = (CooldownInstance) this.cooldowns.get(group);
        if (cooldown != null) {
            float duration = (float) (cooldown.endTime - cooldown.startTime);
            float remaining = (float) cooldown.endTime - ((float) this.tickCount + a);
            return MathHelper.clamp_float(remaining / duration, 0.0F, 1.0F);
        } else {
            return 0.0F;
        }
    }

    public void tick() {
        ++this.tickCount;
        if (!this.cooldowns.isEmpty()) {
            Iterator<Map.Entry<ResourceLocation, CooldownInstance>> iterator = this.cooldowns.entrySet()
                .iterator();

            while (iterator.hasNext()) {
                Map.Entry<ResourceLocation, CooldownInstance> entry = (Map.Entry) iterator.next();
                if ((entry.getValue()).endTime <= this.tickCount) {
                    iterator.remove();
                    this.onCooldownEnded(entry.getKey());
                }
            }
        }

    }

    @Nullable
    public ResourceLocation getCooldownGroup(ItemStack item) {
        if (item == null || item.getItem() == null) return null;
        if (!(item.getItem() instanceof IItemCooldown cooldown)) return null;
        UseCooldown useCooldown = cooldown.getUseCooldown(item);
        if (useCooldown.cooldownGroup()
            .isPresent()) {
            return useCooldown.cooldownGroup()
                .get();
        }
        String itemName = Item.itemRegistry.getNameForObject(item.getItem());
        if (itemName == null || itemName.isEmpty()) return null;
        return new ResourceLocation(itemName);
    }

    public void addCooldown(ItemStack item, int time) {
        ResourceLocation group = this.getCooldownGroup(item);
        if (group == null) return;
        this.addCooldown(group, time);
    }

    public void addCooldown(ResourceLocation cooldownGroup, int time) {
        this.cooldowns.put(cooldownGroup, new CooldownInstance(this.tickCount, this.tickCount + time));
        this.onCooldownStarted(cooldownGroup, time);
    }

    public void removeCooldown(ItemStack item) {
        ResourceLocation group = this.getCooldownGroup(item);
        if (group == null) return;
        this.removeCooldown(group);
    }

    public void removeCooldown(ResourceLocation cooldownGroup) {
        this.cooldowns.remove(cooldownGroup);
        this.onCooldownEnded(cooldownGroup);
    }

    protected void onCooldownStarted(ResourceLocation cooldownGroup, int duration) {}

    protected void onCooldownEnded(ResourceLocation cooldownGroup) {}

    private static class CooldownInstance {

        final int startTime;
        final int endTime;

        CooldownInstance(int startTime, int endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
}

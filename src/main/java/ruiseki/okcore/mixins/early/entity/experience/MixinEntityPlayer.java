
package ruiseki.okcore.mixins.early.entity.experience;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import ruiseki.okcore.entity.experience.IPlayerXp;
import ruiseki.okcore.event.entity.PlayerXpEvent;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer implements IPlayerXp {

    @Shadow
    public int experienceLevel;

    @Shadow
    public float experience;

    @Shadow
    public int experienceTotal;

    @Shadow
    public abstract int xpBarCap();

    @Shadow
    public abstract void addScore(int amount);

    @Shadow
    public abstract void addExperienceLevel(int p_82242_1_);

    @Override
    public void okcore$giveExperiencePoints(int amount) {
        PlayerXpEvent.XpChange event = new PlayerXpEvent.XpChange(this.okcore$getThis(), amount);
        if (MinecraftForge.EVENT_BUS.post(event)) return;
        amount = event.getAmount();

        this.addScore(amount);

        long total = (long) this.experienceTotal + amount;
        this.experienceTotal = (int) Math.clamp(total, 0L, Integer.MAX_VALUE);

        double progress = (double) this.experience + (double) amount / this.okcore$getXpNeededForNextLevel();
        while (progress < 0.0D) {
            double remainingXp = progress * this.okcore$getXpNeededForNextLevel();

            if (this.experienceLevel <= 0) {
                this.okcore$giveExperienceLevels(-1);
                this.experience = 0.0F;
                return;
            }

            int oldLevel = this.experienceLevel;
            this.okcore$giveExperienceLevels(-1);

            if (this.experienceLevel >= oldLevel) {
                this.experience = 0.0F;
                return;
            }

            progress = 1.0D + remainingXp / this.okcore$getXpNeededForNextLevel();
        }

        while (progress >= 1.0D) {
            int cap = this.okcore$getXpNeededForNextLevel();

            // Consume one XP bar using double precision.
            progress = (progress - 1.0D) * cap;

            int oldLevel = this.experienceLevel;
            this.okcore$giveExperienceLevels(1);

            // Event was cancelled or did not produce an effective level change.
            if (this.experienceLevel <= oldLevel) {
                this.experience = (float) (progress / cap);
                return;
            }

            progress /= this.okcore$getXpNeededForNextLevel();
        }

        this.experience = (float) progress;
    }

    @Override
    public void okcore$giveExperienceLevels(int amount) {
        this.addExperienceLevel(amount);
    }

    @WrapMethod(method = "addExperienceLevel(I)V")
    private void okcore$wrapAddExperienceLevel(int p_82242_1_, Operation<Void> original) {
        PlayerXpEvent.LevelChange event = new PlayerXpEvent.LevelChange(this.okcore$getThis(), p_82242_1_);
        if (MinecraftForge.EVENT_BUS.post(event)) return;
        original.call(event.getLevels());
    }

    @WrapMethod(method = "addExperience(I)V")
    private void okcore$wrapAddExperience(int p_71023_1_, Operation<Void> original) {
        PlayerXpEvent.XpChange event = new PlayerXpEvent.XpChange(this.okcore$getThis(), p_71023_1_);
        if (MinecraftForge.EVENT_BUS.post(event)) return;
        original.call(event.getAmount());
    }

    @Override
    public int okcore$getXpNeededForNextLevel() {
        return this.xpBarCap();
    }

    @Unique
    private EntityPlayer okcore$getThis() {
        return (EntityPlayer) (Object) this;
    }
}

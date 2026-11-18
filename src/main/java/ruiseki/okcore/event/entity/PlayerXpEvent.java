package ruiseki.okcore.event.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;
import ruiseki.okcore.entity.experience.IPlayerXp;

/**
 * PlayerXpEvent is fired whenever an event involving player experience occurs. <br>
 * If a method utilizes this {@link cpw.mods.fml.common.eventhandler.Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class PlayerXpEvent extends PlayerEvent {

    public PlayerXpEvent(EntityPlayer player) {
        super(player);
    }

    /**
     * This event is fired when the player's experience changes through the
     * {@link IPlayerXp#okcore$giveExperiencePoints(int)} method.
     * It can be cancelled, and no further processing will be done.
     */
    @Cancelable
    public static class XpChange extends PlayerXpEvent {

        private int amount;

        public XpChange(EntityPlayer player, int amount) {
            super(player);
            this.amount = amount;
        }

        public int getAmount() {
            return this.amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

    }

    /**
     * This event is fired when the player's experience level changes through the
     * {@link IPlayerXp#okcore$giveExperienceLevels(int)} method.
     * It can be cancelled, and no further processing will be done.
     */
    @Cancelable
    public static class LevelChange extends PlayerXpEvent {

        private int levels;

        public LevelChange(EntityPlayer player, int levels) {
            super(player);
            this.levels = levels;
        }

        public int getLevels() {
            return this.levels;
        }

        public void setLevels(int levels) {
            this.levels = levels;
        }

    }
}

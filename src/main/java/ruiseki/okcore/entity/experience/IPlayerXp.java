
package ruiseki.okcore.entity.experience;

public interface IPlayerXp {

    /**
     * Adds or removes experience points from the player.
     *
     * @param amount positive to add XP, negative to remove XP
     */
    void okcore$giveExperiencePoints(int amount);

    /**
     * Gets the amount of experience points required for the next level.
     */
    int okcore$getXpNeededForNextLevel();

    /**
     * Adds or removes experience levels from the player.
     *
     * @param amount positive to add levels, negative to remove levels
     */
    void okcore$giveExperienceLevels(int amount);
}

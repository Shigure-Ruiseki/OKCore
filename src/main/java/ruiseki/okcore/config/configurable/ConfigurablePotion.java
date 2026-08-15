package ruiseki.okcore.config.configurable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.config.extendedconfig.PotionConfig;
import ruiseki.okcore.init.ModBase;

/**
 * A configurable potion effect.
 *
 * @author rubensworks
 */
public abstract class ConfigurablePotion extends Potion implements IConfigurable<PotionConfig> {

    private final ResourceLocation resource;

    protected PotionConfig eConfig = null;

    /**
     * Make a new Enchantment instance
     *
     * @param eConfig   Config for this enchantment.
     * @param badEffect If the potion effect is bad.
     * @param color     The color of the potion.
     * @param iconIndex The sprite index of the icon.
     */
    protected ConfigurablePotion(PotionConfig eConfig, boolean badEffect, int color, int iconIndex) {
        super(eConfig.ID, badEffect, color);
        this.setConfig(eConfig);
        this.setPotionName(eConfig.getUnlocalizedName());
        this.setIconIndex(iconIndex % 8, iconIndex / 8);
        this.resource = new ResourceLocation(
            eConfig.getMod()
                .getModId(),
            eConfig.getMod()
                .getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI) + "potions.png");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(resource);
        return super.getStatusIconIndex();
    }

    private void setConfig(PotionConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public PotionConfig getConfig() {
        return eConfig;
    }

    public boolean isActiveOn(EntityLivingBase entity) {
        return isActiveOn(entity, this);
    }

    public boolean isActiveOn(EntityLivingBase entity, Potion potion) {
        return entity.getActivePotionEffect(potion) != null;
    }

    public int getAmplifier(EntityLivingBase entity, Potion potion) {
        return entity.getActivePotionEffect(potion)
            .getAmplifier();
    }

    public int getAmplifier(EntityLivingBase entity) {
        return getAmplifier(entity, this);
    }

    protected abstract void onUpdate(EntityLivingBase entity);

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.entityLiving;
        if (isActiveOn(entity)) {
            onUpdate(entity);
        }
    }

}

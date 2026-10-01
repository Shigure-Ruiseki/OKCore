package ruiseki.okcore.modcompat.backhand;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.ReflectionHelper;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.helper.ItemHelpers;

public class BackhandHelpers {

    private static final String BACKHAND_UTILS_CLASS = "xonin.backhand.api.core.BackhandUtils";

    private static boolean isLoaded = false;
    private static final MethodHandle getOffhandItem;
    private static final MethodHandle getOffhandSlot;

    static {
        MethodHandle getOffhandItemTemp = null;
        MethodHandle getOffhandSlotTemp = null;

        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            final Class<?> backhandUtilsClass = ReflectionHelper
                .getClass(BackhandHelpers.class.getClassLoader(), BACKHAND_UTILS_CLASS);

            getOffhandItemTemp = lookup.findStatic(
                backhandUtilsClass,
                "getOffhandItem",
                MethodType.methodType(ItemStack.class, EntityPlayer.class));

            getOffhandSlotTemp = lookup
                .findStatic(backhandUtilsClass, "getOffhandSlot", MethodType.methodType(int.class, EntityPlayer.class));

            isLoaded = true;
            OKCore.okLog(Level.INFO, "Backhand compat loaded");
        } catch (Exception e) {
            OKCore.okLog(Level.INFO, "Failed to load Backhand compat", e);
            isLoaded = false;
        }

        getOffhandItem = getOffhandItemTemp;
        getOffhandSlot = getOffhandSlotTemp;
    }

    public static ItemStack getOffhandItem(EntityPlayer player) {
        if (isLoaded && getOffhandItem != null) {
            try {
                return (ItemStack) getOffhandItem.invokeExact(player);
            } catch (Error e) {
                throw e;
            } catch (Throwable t) {
                OKCore.okLog(Level.ERROR, "Failed to invoke Backhand getOffhandItem", t);
                isLoaded = false;
            }
        }
        return null;
    }

    public static int getOffhandSlot(EntityPlayer player) {
        if (isLoaded && getOffhandSlot != null) {
            try {
                return (int) getOffhandSlot.invokeExact(player);
            } catch (Error e) {
                throw e;
            } catch (Throwable t) {
                OKCore.okLog(Level.ERROR, "Failed to invoke Backhand getOffhandSlot", t);
                isLoaded = false;
            }
        }
        return 36;
    }

    public static boolean isOffhand(EntityPlayer player, ItemStack itemStack) {
        if (!isLoaded || itemStack == null) return false;
        ItemStack offhandStack = getOffhandItem(player);
        return ItemHelpers.areItemsEqual(itemStack, offhandStack);
    }
}

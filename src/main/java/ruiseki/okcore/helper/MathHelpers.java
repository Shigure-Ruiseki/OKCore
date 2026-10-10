package ruiseki.okcore.helper;

import net.minecraft.util.MathHelper;

public class MathHelpers extends MathHelper {

    public static int intMaxCappedAddition(int a, int b) {
        return Integer.MAX_VALUE - a < b ? Integer.MAX_VALUE : a + b;
    }

    public static int intMaxCappedMultiply(int a, int b) {
        return Integer.MAX_VALUE / a < b ? Integer.MAX_VALUE : a * b;
    }
}


package ruiseki.okcore.enums;

import java.util.Locale;
import java.util.Map;

import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.ImmutableMap;

import ruiseki.okcore.datastructure.StringRepresentable;

/**
 * Minecraft dye colors in the same ordinal order as AE2's AEColor and vanilla wool:
 * WHITE (0) through BLACK (15), followed by TRANSPARENT (16).
 *
 * <p>
 * Vanilla dye item damage values use the reverse order (BLACK = 0, WHITE = 15).
 * Use {@link #toDyeIndex()} and {@link #fromDyeIndex(int)} to convert instead
 * of using {@link #ordinal()} as an item damage value.
 *
 * <p>
 * RGB is packed as {@code 0xRRGGBB}; packed formats such as ARGB and ABGR
 * describe integer bit layout, not the order of bytes in native memory.
 * HSV and HSL use hue in degrees [0, 360) and saturation/value/lightness in [0, 1].
 *
 * <p>
 * TRANSPARENT has white RGB components but an alpha value of zero. Methods
 * that output RGB, HSV or HSL cannot represent transparency on their own.
 *
 * <p>
 * Changing enum order or serialized names is a breaking data-format change.
 */
public enum EnumDye implements StringRepresentable, ICyclicEnum<EnumDye> {

    // spotless:off
    WHITE       ("white",       "dyeWhite"),
    ORANGE      ("orange",      "dyeOrange"),
    MAGENTA     ("magenta",     "dyeMagenta"),
    LIGHT_BLUE  ("light_blue",  "dyeLightBlue"),
    YELLOW      ("yellow",      "dyeYellow"),
    LIME        ("lime",        "dyeLime"),
    PINK        ("pink",        "dyePink"),
    GRAY        ("gray",        "dyeGray"),
    LIGHT_GRAY  ("light_gray",  "dyeLightGray"),
    CYAN        ("cyan",        "dyeCyan"),
    PURPLE      ("purple",      "dyePurple"),
    BLUE        ("blue",        "dyeBlue"),
    BROWN       ("brown",       "dyeBrown"),
    GREEN       ("green",       "dyeGreen"),
    RED         ("red",         "dyeRed"),
    BLACK       ("black",       "dyeBlack"),
    TRANSPARENT ("transparent", null);
    // spotless:on

    /** Number of actual dye colors; excludes {@link #TRANSPARENT}. */
    private static final int COLOR_COUNT = 16;

    /** Cached constants in wool/AE2 color order. */
    private static final EnumDye[] VALUES = values();
    /** Immutable lookup by serialized name (e.g. {@code light_blue}). */
    private static final Map<String, EnumDye> NAME_VALUES;

    /**
     * Ore dictionary names indexed by wool/AE2 color index (0-15).
     * This public array is mutable; callers must not modify its contents.
     */
    public static final String[] DYE_ORE_NAMES = new String[COLOR_COUNT];

    static {
        ImmutableMap.Builder<String, EnumDye> builder = ImmutableMap.builder();

        for (EnumDye value : VALUES) {
            builder.put(value.getSerializedName(), value);

            if (value.isColored()) {
                DYE_ORE_NAMES[value.ordinal()] = value.oreName;
            }
        }

        NAME_VALUES = builder.build();
    }

    /** Stable lowercase identifier used for serialization and localization. */
    private final String name;
    /** Forge OreDictionary identifier; null for transparent. */
    private final String oreName;

    /**
     * Creates a dye constant.
     *
     * @param name    stable serialized name
     * @param oreName ore dictionary name, or null for transparent
     */
    EnumDye(String name, String oreName) {
        this.name = name;
        this.oreName = oreName;
    }

    @Override
    /**
     * Returns the stable lowercase identifier for persistent storage.
     *
     * @return serialized name, such as {@code light_blue}
     */
    public String getSerializedName() {
        return name;
    }

    @Override
    /**
     * Returns the cached enum constants used by {@link ICyclicEnum}.
     * <p>
     * The returned array is shared and must not be modified.
     *
     * @return the shared array in wool/AE2 color order
     */
    public EnumDye[] getEnumValues() {
        return VALUES;
    }

    /**
     * Looks up a wool/AE2 color index, including transparent at index 16.
     *
     * @param ordinal enum index
     * @return matching color, or {@link #TRANSPARENT} for an invalid index
     */
    public static EnumDye fromOrdinal(int ordinal) {
        return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : TRANSPARENT;
    }

    /**
     * Finds a color by its case-sensitive {@link #getSerializedName()} value.
     *
     * @param name serialized name
     * @return matching color, or {@link #TRANSPARENT} if unknown
     */
    public static EnumDye fromName(String name) {
        return name == null ? TRANSPARENT : NAME_VALUES.getOrDefault(name, TRANSPARENT);
    }

    /**
     * Alias for {@link #fromOrdinal(int)}.
     *
     * @param index numeric color index
     * @return matching color or transparent
     */
    public static EnumDye fromIndex(int index) {
        return fromOrdinal(index);
    }

    /**
     * Looks up a color by wool/AE2 index (white 0, black 15, transparent 16).
     *
     * @param index numeric color index
     * @return matching color, or transparent when invalid
     */
    public static EnumDye fromColorIndex(int index) {
        return fromOrdinal(index);
    }

    /**
     * Converts vanilla dye item damage (black 0 to white 15) to this enum.
     *
     * @param index numeric color index
     * @return matching dye, or transparent when invalid
     */
    public static EnumDye fromDyeIndex(int index) {
        return index >= 0 && index < COLOR_COUNT ? VALUES[COLOR_COUNT - 1 - index] : TRANSPARENT;
    }

    /**
     * Returns the wool metadata / AE2 color index.
     *
     * @return ordinal in the range 0-16
     */
    public int toColorIndex() {
        return ordinal();
    }

    /**
     * Converts to vanilla dye item damage, which is reverse-ordered.
     *
     * @return dye index 0-15, or -1 for transparent
     */
    public int toDyeIndex() {
        return isColored() ? COLOR_COUNT - 1 - ordinal() : -1;
    }

    /**
     * Checks whether this constant represents an actual dye.
     *
     * @return true for any of the 16 dyes, false for transparent
     */
    public boolean isColored() {
        return this != TRANSPARENT;
    }

    /**
     * Returns the Forge ore dictionary identifier for this dye.
     *
     * @return ore dictionary name, or null for transparent
     */
    public String getOreName() {
        return oreName;
    }

    /**
     * Matches an item stack against registered dye ore dictionary entries.
     *
     * @param stack input item stack
     * @return matching enum color, or null when no dye entry matches
     */
    public static EnumDye getColorFromDye(ItemStack stack) {
        if (stack == null) return null;

        int[] oreIDs = OreDictionary.getOreIDs(stack);

        for (int i = 0; i < COLOR_COUNT; i++) {
            int dyeID = OreDictionary.getOreID(DYE_ORE_NAMES[i]);

            for (int oreID : oreIDs) {
                if (dyeID == oreID) {
                    return VALUES[i];
                }
            }
        }

        return null;
    }

    /**
     * Returns the vanilla dye color as packed RGB. Transparent uses white as an untinted fallback.
     *
     * @return packed {@code 0xRRGGBB}
     */
    public int getColor() {
        return isColored() ? ItemDye.field_150922_c[toDyeIndex()] : 0xFFFFFF;
    }

    /**
     * Returns Minecraft's legacy vanilla dye name.
     *
     * @return vanilla name, or {@code transparent}
     */
    public String getName() {
        return isColored() ? ItemDye.field_150921_b[toDyeIndex()] : "transparent";
    }

    /**
     * Returns the translation key used to localize this dye in OKCore.
     *
     * @return key in the form {@code gui.okcore.dye.red.name}
     */
    public String getUnlocalizedName() {
        return "gui.okcore.dye." + name + ".name";
    }

    /**
     * Compatibility alias for the misspelled method name.
     *
     * @return the same key as {@link #getUnlocalizedName()}
     * @deprecated use {@link #getUnlocalizedName()}
     */
    @Deprecated
    public String getUnlocatedName() {
        return getUnlocalizedName();
    }

    @Override
    /**
     * Returns the stable serialized name rather than the enum constant name.
     *
     * @return serialized name
     */
    public String toString() {
        return getSerializedName();
    }

    // ---------------------------------------------------------------------
    // Color components
    // ---------------------------------------------------------------------

    /**
     * Extracts the red RGB channel.
     *
     * @return red component from 0 to 255
     */
    public int getRed() {
        return (getColor() >>> 16) & 0xFF;
    }

    /**
     * Extracts the green RGB channel.
     *
     * @return green component from 0 to 255
     */
    public int getGreen() {
        return (getColor() >>> 8) & 0xFF;
    }

    /**
     * Extracts the blue RGB channel.
     *
     * @return blue component from 0 to 255
     */
    public int getBlue() {
        return getColor() & 0xFF;
    }

    /**
     * Returns a logical alpha channel for this dye.
     *
     * @return 0 for transparent, otherwise 255
     */
    public int getAlpha() {
        return isColored() ? 255 : 0;
    }

    // ---------------------------------------------------------------------
    // Instance color conversions
    // ---------------------------------------------------------------------

    /**
     * Returns this dye as packed RGB without alpha.
     *
     * @return packed {@code 0xRRGGBB}
     */
    public int dyeToRgb() {
        return toRgb(getColor());
    }

    /**
     * Returns this dye as packed ARGB including transparency.
     *
     * @return packed {@code 0xAARRGGBB}
     */
    public int dyeToArgb() {
        return rgbToArgb(getColor(), getAlpha());
    }

    /**
     * Returns this dye as packed RGBA including transparency.
     *
     * @return packed {@code 0xRRGGBBAA}
     */
    public int dyeToRgba() {
        return rgbToRgba(getColor(), getAlpha());
    }

    /**
     * Returns this dye as packed BGR without alpha.
     *
     * @return packed {@code 0xBBGGRR}
     */
    public int dyeToBgr() {
        return rgbToBgr(getColor());
    }

    /**
     * Returns this dye as packed ABGR including transparency.
     *
     * @return packed {@code 0xAABBGGRR}
     */
    public int dyeToAbgr() {
        return rgbToAbgr(getColor(), getAlpha());
    }

    /**
     * Returns this dye as packed BGRA including transparency.
     *
     * @return packed {@code 0xBBGGRRAA}
     */
    public int dyeToBgra() {
        return rgbToBgra(getColor(), getAlpha());
    }

    /**
     * Formats this dye as a CSS-style RGB hex string; alpha is omitted.
     *
     * @return hex string in the form {@code #RRGGBB}
     */
    public String dyeToHex() {
        return rgbToHexString(getColor());
    }

    /**
     * Formats this dye as a CSS-style RGBA hex string.
     *
     * @return hex string in the form {@code #RRGGBBAA}
     */
    public String dyeToHexa() {
        return rgbaToHexString(getColor(), getAlpha());
    }

    /**
     * Converts this dye RGB to HSV; does not encode transparency.
     *
     * @return new array {hue degrees, saturation 0-1, value 0-1}
     */
    public float[] dyeToHsv() {
        return rgbToHsv(getColor());
    }

    /**
     * Alias for {@link #dyeToHsv()}; brightness equals HSV value.
     *
     * @return new array {hue degrees, saturation 0-1, brightness 0-1}
     */
    public float[] dyeToHsb() {
        return dyeToHsv();
    }

    /**
     * Converts this dye RGB to HSL; does not encode transparency.
     *
     * @return new array {hue degrees, saturation 0-1, lightness 0-1}
     */
    public float[] dyeToHsl() {
        return rgbToHsl(getColor());
    }

    /**
     * Returns individual RGB channels.
     *
     * @return new array {red, green, blue}, each 0-255
     */
    public int[] dyeToRgbComponents() {
        return new int[] { getRed(), getGreen(), getBlue() };
    }

    /**
     * Returns individual RGBA channels.
     *
     * @return new array {red, green, blue, alpha}, each 0-255
     */
    public int[] dyeToRgbaComponents() {
        return new int[] { getRed(), getGreen(), getBlue(), getAlpha() };
    }

    // ---------------------------------------------------------------------
    // RGB packing
    // ---------------------------------------------------------------------

    /**
     * Packs RGB components or strips the leading alpha of packed ARGB.
     *
     * @param r red component 0-255
     * @param g green component 0-255
     * @param b blue component 0-255 or HSB brightness
     * @return packed {@code 0xRRGGBB}
     */
    public static int toRgb(int r, int g, int b) {
        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * Packs RGB components or strips the leading alpha of packed ARGB.
     *
     * @param color packed color integer
     * @return packed {@code 0xRRGGBB}
     */
    public static int toRgb(int color) {
        return color & 0xFFFFFF;
    }

    /**
     * Packs channels into an ARGB integer.
     *
     * @param a alpha 0-255 in ARGB/ABGR packing, otherwise a component
     * @param r red component 0-255
     * @param g green component 0-255
     * @param b blue component 0-255 or HSB brightness
     * @return packed {@code 0xAARRGGBB}
     */
    public static int toArgb(int a, int r, int g, int b) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * Packs channels into an RGBA integer.
     *
     * @param r red component 0-255
     * @param g green component 0-255
     * @param b blue component 0-255 or HSB brightness
     * @param a alpha 0-255 in ARGB/ABGR packing, otherwise a component
     * @return packed {@code 0xRRGGBBAA}
     */
    public static int toRgba(int r, int g, int b, int a) {
        return ((r & 0xFF) << 24) | ((g & 0xFF) << 16) | ((b & 0xFF) << 8) | (a & 0xFF);
    }

    /**
     * Packs channels into a BGR integer.
     *
     * @param r red component 0-255
     * @param g green component 0-255
     * @param b blue component 0-255 or HSB brightness
     * @return packed {@code 0xBBGGRR}
     */
    public static int toBgr(int r, int g, int b) {
        return ((b & 0xFF) << 16) | ((g & 0xFF) << 8) | (r & 0xFF);
    }

    /**
     * Packs channels into an ABGR integer.
     *
     * @param a alpha 0-255 in ARGB/ABGR packing, otherwise a component
     * @param r red component 0-255
     * @param g green component 0-255
     * @param b blue component 0-255 or HSB brightness
     * @return packed {@code 0xAABBGGRR}
     */
    public static int toAbgr(int a, int r, int g, int b) {
        return ((a & 0xFF) << 24) | ((b & 0xFF) << 16) | ((g & 0xFF) << 8) | (r & 0xFF);
    }

    /**
     * Packs channels into a BGRA integer.
     *
     * @param b blue component 0-255 or HSB brightness
     * @param g green component 0-255
     * @param r red component 0-255
     * @param a alpha 0-255 in ARGB/ABGR packing, otherwise a component
     * @return packed {@code 0xBBGGRRAA}
     */
    public static int toBgra(int b, int g, int r, int a) {
        return ((b & 0xFF) << 24) | ((g & 0xFF) << 16) | ((r & 0xFF) << 8) | (a & 0xFF);
    }

    // ---------------------------------------------------------------------
    // RGB conversions
    // ---------------------------------------------------------------------

    /**
     * Adds an alpha channel to packed RGB (default alpha is 255).
     *
     * @param rgb packed input RGB {@code 0xRRGGBB}
     * @return packed {@code 0xAARRGGBB}
     */
    public static int rgbToArgb(int rgb) {
        return rgbToArgb(rgb, 255);
    }

    /**
     * Adds an alpha channel to packed RGB (default alpha is 255).
     *
     * @param rgb   packed input RGB {@code 0xRRGGBB}
     * @param alpha alpha 0-255
     * @return packed {@code 0xAARRGGBB}
     */
    public static int rgbToArgb(int rgb, int alpha) {
        return ((alpha & 0xFF) << 24) | (rgb & 0xFFFFFF);
    }

    /**
     * Adds an alpha channel to packed RGB (default alpha is 255).
     *
     * @param rgb packed input RGB {@code 0xRRGGBB}
     * @return packed {@code 0xRRGGBBAA}
     */
    public static int rgbToRgba(int rgb) {
        return rgbToRgba(rgb, 255);
    }

    /**
     * Adds an alpha channel to packed RGB (default alpha is 255).
     *
     * @param rgb   packed input RGB {@code 0xRRGGBB}
     * @param alpha alpha 0-255
     * @return packed {@code 0xRRGGBBAA}
     */
    public static int rgbToRgba(int rgb, int alpha) {
        return ((rgb & 0xFFFFFF) << 8) | (alpha & 0xFF);
    }

    /**
     * Reverses the red/blue channels of packed RGB.
     *
     * @param rgb packed input RGB {@code 0xRRGGBB}
     * @return packed {@code 0xBBGGRR}
     */
    public static int rgbToBgr(int rgb) {
        int r = (rgb >>> 16) & 0xFF;
        int g = (rgb >>> 8) & 0xFF;
        int b = rgb & 0xFF;

        return toBgr(r, g, b);
    }

    /**
     * Converts packed RGB to ABGR; default alpha is 255.
     *
     * @param rgb packed input RGB {@code 0xRRGGBB}
     * @return packed {@code 0xAABBGGRR}
     */
    public static int rgbToAbgr(int rgb) {
        return rgbToAbgr(rgb, 255);
    }

    /**
     * Converts packed RGB to ABGR; default alpha is 255.
     *
     * @param rgb   packed input RGB {@code 0xRRGGBB}
     * @param alpha alpha 0-255
     * @return packed {@code 0xAABBGGRR}
     */
    public static int rgbToAbgr(int rgb, int alpha) {
        return ((alpha & 0xFF) << 24) | rgbToBgr(rgb);
    }

    /**
     * Converts packed RGB to ABGR; default alpha is 255.
     *
     * @param r red component 0-255
     * @param g green component 0-255
     * @param b blue component 0-255 or HSB brightness
     * @return packed {@code 0xAABBGGRR}
     */
    public static int rgbToAbgr(int r, int g, int b) {
        return toAbgr(255, r, g, b);
    }

    /**
     * Converts packed RGB to BGRA; default alpha is 255.
     *
     * @param rgb packed input RGB {@code 0xRRGGBB}
     * @return packed {@code 0xBBGGRRAA}
     */
    public static int rgbToBgra(int rgb) {
        return rgbToBgra(rgb, 255);
    }

    /**
     * Converts packed RGB to BGRA; default alpha is 255.
     *
     * @param rgb   packed input RGB {@code 0xRRGGBB}
     * @param alpha alpha 0-255
     * @return packed {@code 0xBBGGRRAA}
     */
    public static int rgbToBgra(int rgb, int alpha) {
        return (rgbToBgr(rgb) << 8) | (alpha & 0xFF);
    }

    /**
     * Drops alpha from packed ARGB.
     *
     * @param argb input argb
     * @return packed {@code 0xRRGGBB}
     */
    public static int argbToRgb(int argb) {
        return argb & 0xFFFFFF;
    }

    /**
     * Drops alpha from packed RGBA.
     *
     * @param rgba input rgba
     * @return packed {@code 0xRRGGBB}
     */
    public static int rgbaToRgb(int rgba) {
        return (rgba >>> 8) & 0xFFFFFF;
    }

    /**
     * Swaps red and blue channels of packed BGR.
     *
     * @param bgr input bgr
     * @return packed {@code 0xRRGGBB}
     */
    public static int bgrToRgb(int bgr) {
        return rgbToBgr(bgr);
    }

    /**
     * Drops alpha and swaps red and blue from packed ABGR.
     *
     * @param abgr input abgr
     * @return packed {@code 0xRRGGBB}
     */
    public static int abgrToRgb(int abgr) {
        return rgbToBgr(abgr);
    }

    /**
     * Drops alpha and swaps red and blue from packed BGRA.
     *
     * @param bgra input bgra
     * @return packed {@code 0xRRGGBB}
     */
    public static int bgraToRgb(int bgra) {
        return rgbToBgr(bgra >>> 8);
    }

    // ---------------------------------------------------------------------
    // HEX conversions
    // ---------------------------------------------------------------------

    /**
     * Legacy method: despite the name, returns packed opaque ARGB rather than a string.
     *
     * @param r red component 0-255
     * @param g green component 0-255
     * @param b blue component 0-255 or HSB brightness
     * @return packed {@code 0xFFRRGGBB}
     */
    public static int rgbToHex(int r, int g, int b) {
        return toArgb(255, r, g, b);
    }

    /**
     * Formats packed RGB as uppercase hexadecimal.
     *
     * @param rgb packed input RGB {@code 0xRRGGBB}
     * @return string in the form {@code #RRGGBB}
     */
    public static String rgbToHexString(int rgb) {
        return String.format(Locale.ROOT, "#%06X", rgb & 0xFFFFFF);
    }

    /**
     * Formats RGB and alpha as uppercase CSS hexadecimal.
     *
     * @param rgb   packed input RGB {@code 0xRRGGBB}
     * @param alpha alpha 0-255
     * @return string in the form {@code #RRGGBBAA}
     */
    public static String rgbaToHexString(int rgb, int alpha) {
        return String.format(Locale.ROOT, "#%08X", rgbToRgba(rgb, alpha));
    }

    /**
     * Parses CSS shorthand or full RGB(A) hex and discards alpha.
     *
     * @param hex CSS-style hexadecimal color string
     * @return packed {@code 0xRRGGBB}
     * @throws IllegalArgumentException if the input is malformed
     */
    public static int hexToRgb(String hex) {
        return (hexToRgba(hex) >>> 8) & 0xFFFFFF;
    }

    /**
     * Parses CSS-style #RGB, #RGBA, #RRGGBB or #RRGGBBAA (leading # optional).
     *
     * @param hex CSS-style hexadecimal color string
     * @return packed {@code 0xRRGGBBAA}
     * @throws IllegalArgumentException if the input is malformed
     */
    public static int hexToRgba(String hex) {
        if (hex == null) {
            throw new IllegalArgumentException("Hex color cannot be null");
        }

        String value = hex.startsWith("#") ? hex.substring(1) : hex;

        switch (value.length()) {
            case 3: {
                int r = parseHexNibble(value.charAt(0)) * 17;
                int g = parseHexNibble(value.charAt(1)) * 17;
                int b = parseHexNibble(value.charAt(2)) * 17;
                return toRgba(r, g, b, 255);
            }
            case 4: {
                int r = parseHexNibble(value.charAt(0)) * 17;
                int g = parseHexNibble(value.charAt(1)) * 17;
                int b = parseHexNibble(value.charAt(2)) * 17;
                int a = parseHexNibble(value.charAt(3)) * 17;
                return toRgba(r, g, b, a);
            }
            case 6:
                return (int) ((Long.parseLong(value, 16) << 8) | 0xFFL);
            case 8:
                return (int) Long.parseLong(value, 16);
            default:
                throw new IllegalArgumentException("Invalid hex color: " + hex);
        }
    }

    /**
     * Parses one hexadecimal digit.
     *
     * @param character hexadecimal character
     * @return integer from 0 to 15
     * @throws IllegalArgumentException if the input is malformed
     */
    private static int parseHexNibble(char character) {
        int value = Character.digit(character, 16);
        if (value < 0) {
            throw new IllegalArgumentException("Invalid hex digit: " + character);
        }
        return value;
    }

    // ---------------------------------------------------------------------
    // HSV / HSB conversions
    // ---------------------------------------------------------------------

    /**
     * Converts packed RGB to HSV.
     *
     * @param rgb packed input RGB {@code 0xRRGGBB}
     * @return new array {hue [0,360), saturation [0,1], value [0,1]}
     */
    public static float[] rgbToHsv(int rgb) {
        float r = ((rgb >>> 16) & 0xFF) / 255.0f;
        float g = ((rgb >>> 8) & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float h = calculateHue(r, g, b, max, delta);
        float s = max == 0.0f ? 0.0f : delta / max;

        return new float[] { h, s, max };
    }

    /**
     * Alias for {@link #rgbToHsv(int)}.
     *
     * @param rgb packed input RGB {@code 0xRRGGBB}
     * @return HSV/HSB components
     */
    public static float[] rgbToHsb(int rgb) {
        return rgbToHsv(rgb);
    }

    /**
     * Converts HSV to packed RGB; hue wraps, saturation and value are clamped.
     *
     * @param h hue in degrees
     * @param s saturation in [0,1]
     * @param v value in [0,1]
     * @return packed {@code 0xRRGGBB}
     */
    public static int hsvToRgb(float h, float s, float v) {
        h = normalizeHue(h);
        s = clamp01(s);
        v = clamp01(v);

        float c = v * s;
        float x = c * (1.0f - Math.abs((h / 60.0f) % 2.0f - 1.0f));
        float m = v - c;

        return hueToRgb(h, c, x, m);
    }

    /**
     * Alias for {@link #hsvToRgb(float, float, float)}.
     *
     * @param h hue in degrees
     * @param s saturation in [0,1]
     * @param b blue component 0-255 or HSB brightness
     * @return packed {@code 0xRRGGBB}
     */
    public static int hsbToRgb(float h, float s, float b) {
        return hsvToRgb(h, s, b);
    }

    // ---------------------------------------------------------------------
    // HSL conversions
    // ---------------------------------------------------------------------

    /**
     * Converts packed RGB to HSL.
     *
     * @param rgb packed input RGB {@code 0xRRGGBB}
     * @return new array {hue [0,360), saturation [0,1], lightness [0,1]}
     */
    public static float[] rgbToHsl(int rgb) {
        float r = ((rgb >>> 16) & 0xFF) / 255.0f;
        float g = ((rgb >>> 8) & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        float h = calculateHue(r, g, b, max, delta);
        float l = (max + min) / 2.0f;
        float s = delta == 0.0f ? 0.0f : delta / (1.0f - Math.abs(2.0f * l - 1.0f));

        return new float[] { h, s, l };
    }

    /**
     * Converts HSL to packed RGB; hue wraps, saturation and lightness are clamped.
     *
     * @param h hue in degrees
     * @param s saturation in [0,1]
     * @param l lightness in [0,1]
     * @return packed {@code 0xRRGGBB}
     */
    public static int hslToRgb(float h, float s, float l) {
        h = normalizeHue(h);
        s = clamp01(s);
        l = clamp01(l);

        float c = (1.0f - Math.abs(2.0f * l - 1.0f)) * s;
        float x = c * (1.0f - Math.abs((h / 60.0f) % 2.0f - 1.0f));
        float m = l - c / 2.0f;

        return hueToRgb(h, c, x, m);
    }

    // ---------------------------------------------------------------------
    // Internal color helpers
    // ---------------------------------------------------------------------

    /**
     * Calculates hue from normalized RGB channels and their maximum and delta.
     *
     * @param r     red component 0-255
     * @param g     green component 0-255
     * @param b     blue component 0-255 or HSB brightness
     * @param max   input max
     * @param delta input delta
     * @return hue in degrees [0,360)
     */
    private static float calculateHue(float r, float g, float b, float max, float delta) {
        if (delta == 0.0f) {
            return 0.0f;
        }

        float h;

        if (max == r) {
            h = ((g - b) / delta) % 6.0f;
        } else if (max == g) {
            h = ((b - r) / delta) + 2.0f;
        } else {
            h = ((r - g) / delta) + 4.0f;
        }

        return normalizeHue(h * 60.0f);
    }

    /**
     * Converts chroma, secondary component and offset to RGB using a hue sector.
     *
     * @param h hue in degrees
     * @param c input c
     * @param x input x
     * @param m input m
     * @return packed {@code 0xRRGGBB}
     */
    private static int hueToRgb(float h, float c, float x, float m) {
        float r, g, b;

        if (h < 60.0f) {
            r = c;
            g = x;
            b = 0;
        } else if (h < 120.0f) {
            r = x;
            g = c;
            b = 0;
        } else if (h < 180.0f) {
            r = 0;
            g = c;
            b = x;
        } else if (h < 240.0f) {
            r = 0;
            g = x;
            b = c;
        } else if (h < 300.0f) {
            r = x;
            g = 0;
            b = c;
        } else {
            r = c;
            g = 0;
            b = x;
        }

        return toRgb(Math.round((r + m) * 255.0f), Math.round((g + m) * 255.0f), Math.round((b + m) * 255.0f));
    }

    /**
     * Wraps a hue angle into [0,360).
     *
     * @param hue hue angle in degrees
     * @return normalized hue in degrees
     */
    private static float normalizeHue(float hue) {
        hue %= 360.0f;
        return hue < 0.0f ? hue + 360.0f : hue;
    }

    /**
     * Clamps a numeric component to [0,1].
     *
     * @param value numeric input
     * @return clamped value
     */
    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}


package ruiseki.okcore.client.gui.tooltip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.helper.LangHelpers;

public final class TooltipBuilder {

    public enum ElementType {
        TEXT,
        NEW_LINE,
        SPACE,
        SPACER,
        CUSTOM
    }

    public static final class TooltipElement {

        private final ElementType type;
        private final Object value;

        private TooltipElement(ElementType type, Object value) {
            this.type = type;
            this.value = value;
        }

        public ElementType getType() {
            return this.type;
        }

        public Object getValue() {
            return this.value;
        }

        public String getText() {
            return this.type == ElementType.TEXT ? (String) this.value : null;
        }

        public int getSpace() {
            return this.type == ElementType.SPACER ? (int) this.value : 0;
        }
    }

    public static final class TooltipData {

        private final List<TooltipElement> elements;

        private TooltipData(List<TooltipElement> elements) {
            this.elements = Collections.unmodifiableList(new ArrayList<>(elements));
        }

        public List<TooltipElement> getElements() {
            return this.elements;
        }

        public List<String> getTextLines() {
            List<String> result = new ArrayList<>();

            for (TooltipElement element : this.elements) {
                switch (element.type) {
                    case TEXT:
                        result.add((String) element.value);
                        break;
                    case NEW_LINE:
                        result.add("");
                        break;
                    default:
                        break;
                }
            }

            return result;
        }
    }

    @FunctionalInterface
    public interface TooltipProvider {

        void accept(TooltipBuilder builder);
    }

    private final List<TooltipElement> template = new ArrayList<>();
    private final List<TooltipProvider> providers = new ArrayList<>();

    private List<TooltipElement> elements = this.template;

    private ItemStack stack;
    private EntityPlayer player;
    private boolean advanced;

    private int cursor;
    private boolean cursorLocked;

    // ---------------------------------------------------------------------
    // Context
    // ---------------------------------------------------------------------

    public ItemStack getStack() {
        return this.stack;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }

    public boolean isAdvanced() {
        return this.advanced;
    }

    public List<TooltipElement> getElements() {
        return Collections.unmodifiableList(this.elements);
    }

    // ---------------------------------------------------------------------
    // Content
    // ---------------------------------------------------------------------

    private void insert(TooltipElement element) {
        this.insertAt(this.cursor, element);

        if (!this.cursorLocked) {
            this.cursor++;
        }
    }

    private void insertAt(int index, TooltipElement element) {
        int position = Math.max(0, Math.min(index, this.elements.size()));

        this.elements.add(position, element);

        if (position < this.cursor) {
            this.cursor++;
        }
    }

    public TooltipBuilder add(String text) {
        return addText(text);
    }

    public TooltipBuilder add(String key, Object... params) {
        return addText(LangHelpers.localize(key, params));
    }

    public TooltipBuilder addText(String text) {
        if (text != null) {
            this.insert(new TooltipElement(ElementType.TEXT, text));
        }

        return this;
    }

    public TooltipBuilder addLine(String text) {
        return addText(text).newLine();
    }

    public TooltipBuilder addLine(String key, Object... params) {
        return addLine(LangHelpers.localize(key, params));
    }

    public TooltipBuilder addLines(String... lines) {
        if (lines == null) {
            return this;
        }

        for (String line : lines) {
            addLine(line);
        }

        return this;
    }

    public TooltipBuilder addElement(Object element) {
        if (element != null) {
            this.insert(new TooltipElement(ElementType.CUSTOM, element));
        }

        return this;
    }

    // ---------------------------------------------------------------------
    // Indexed Content
    // ---------------------------------------------------------------------

    public TooltipBuilder add(int index, String text) {
        if (text != null) {
            this.insertAt(index, new TooltipElement(ElementType.TEXT, text));
        }

        return this;
    }

    public TooltipBuilder add(int index, String key, Object... params) {
        return add(index, LangHelpers.localize(key, params));
    }

    public TooltipBuilder addLine(int index, String text) {
        if (text != null) {
            this.insertAt(index, new TooltipElement(ElementType.TEXT, text));
            this.insertAt(index + 1, new TooltipElement(ElementType.NEW_LINE, null));
        }

        return this;
    }

    public TooltipBuilder addLine(int index, String key, Object... params) {
        return addLine(index, LangHelpers.localize(key, params));
    }

    public TooltipBuilder addElement(int index, Object element) {
        if (element != null) {
            this.insertAt(index, new TooltipElement(ElementType.CUSTOM, element));
        }

        return this;
    }

    public TooltipBuilder replace(int index, String text) {
        if (index >= 0 && index < this.elements.size() && text != null) {
            this.elements.set(index, new TooltipElement(ElementType.TEXT, text));
        }

        return this;
    }

    public TooltipBuilder replace(int index, String key, Object... params) {
        return replace(index, LangHelpers.localize(key, params));
    }

    public TooltipBuilder replaceElement(int index, Object element) {
        if (index >= 0 && index < this.elements.size() && element != null) {
            this.elements.set(index, new TooltipElement(ElementType.CUSTOM, element));
        }

        return this;
    }

    public TooltipBuilder remove(int index) {
        if (index >= 0 && index < this.elements.size()) {
            this.elements.remove(index);

            if (index < this.cursor) {
                this.cursor--;
            }

            this.cursor = Math.clamp(this.cursor, 0, this.elements.size());
        }

        return this;
    }

    public TooltipBuilder removeLine(int index) {
        return remove(index);
    }

    // ---------------------------------------------------------------------
    // Spacing
    // ---------------------------------------------------------------------

    public TooltipBuilder newLine() {
        this.insert(new TooltipElement(ElementType.NEW_LINE, null));
        return this;
    }

    public TooltipBuilder space() {
        this.insert(new TooltipElement(ElementType.SPACE, null));
        return this;
    }

    public TooltipBuilder spaceLine(int pixels) {
        this.insert(new TooltipElement(ElementType.SPACER, Math.max(0, pixels)));
        return this;
    }

    public TooltipBuilder spaceLine() {
        return spaceLine(2);
    }

    public TooltipBuilder emptyLine() {
        return spaceLine(9);
    }

    // ---------------------------------------------------------------------
    // Providers
    // ---------------------------------------------------------------------

    public TooltipBuilder addProvider(TooltipProvider provider) {
        this.providers.add(Objects.requireNonNull(provider, "provider"));
        return this;
    }

    public TooltipBuilder clearProviders() {
        this.providers.clear();
        return this;
    }

    // ---------------------------------------------------------------------
    // Remove and Replace
    // ---------------------------------------------------------------------

    public TooltipBuilder removeLine(String text) {
        this.elements.removeIf(element -> element.type == ElementType.TEXT && Objects.equals(element.value, text));

        this.cursor = Math.min(this.cursor, this.elements.size());
        return this;
    }

    public TooltipBuilder removeIf(Predicate<TooltipElement> predicate) {
        this.elements.removeIf(Objects.requireNonNull(predicate, "predicate"));

        this.cursor = Math.min(this.cursor, this.elements.size());
        return this;
    }

    public TooltipBuilder removeMatching(String regex) {
        Pattern pattern = Pattern.compile(regex);

        return removeIf(
            element -> element.type == ElementType.TEXT && pattern.matcher((String) element.value)
                .find());
    }

    public TooltipBuilder replace(String regex, String replacement) {
        Pattern pattern = Pattern.compile(regex);

        int index = this.findNextText(
            this.cursor,
            true,
            text -> pattern.matcher(text)
                .find());

        if (index >= 0) {
            if (replacement == null) {
                this.remove(index);
            } else {
                this.replace(index, replacement);
                this.cursor = Math.min(index + 1, this.elements.size());
            }
        }

        return this;
    }

    public TooltipBuilder replace(String regex, String key, Object... params) {
        return replace(regex, LangHelpers.localize(key, params));
    }

    public TooltipBuilder clearText() {
        this.elements.removeIf(element -> element.type == ElementType.TEXT);
        this.cursor = Math.min(this.cursor, this.elements.size());

        return this;
    }

    public TooltipBuilder clear() {
        this.elements.clear();
        this.cursor = 0;
        this.cursorLocked = false;

        return this;
    }

    // ---------------------------------------------------------------------
    // Cursor
    // ---------------------------------------------------------------------

    private int findNextText(int start, boolean wrapAround, Predicate<String> predicate) {
        int index = start;
        int limit = this.elements.size();

        while (index < limit) {
            TooltipElement element = this.elements.get(index);

            if (element.type == ElementType.TEXT && predicate.test((String) element.value)) {
                return index;
            }

            index++;

            if (index == limit && wrapAround) {
                index = 0;
                limit = start;
                wrapAround = false;
            }
        }

        return -1;
    }

    public TooltipBuilder moveCursorAfterElement(String regex) {
        return moveCursorAfterElement(Pattern.compile(regex));
    }

    public TooltipBuilder moveCursorAfterElement(Pattern pattern) {
        int index = this.findNextText(
            this.cursor,
            true,
            text -> pattern.matcher(text)
                .find());

        this.cursor = index < 0 ? this.elements.size() : index + 1;

        return this;
    }

    public TooltipBuilder moveCursorToStart() {
        this.cursor = 0;
        return this;
    }

    public TooltipBuilder moveCursorToEnd() {
        this.cursor = this.elements.size();
        return this;
    }

    public TooltipBuilder moveCursorForward(int amount) {
        this.cursor = Math.min(this.cursor + Math.max(0, amount), this.elements.size());

        return this;
    }

    public TooltipBuilder moveCursorForward() {
        return moveCursorForward(1);
    }

    public TooltipBuilder moveCursorBackward(int amount) {
        this.cursor = Math.max(0, this.cursor - Math.max(0, amount));

        return this;
    }

    public TooltipBuilder moveCursorBackward() {
        return moveCursorBackward(1);
    }

    public TooltipBuilder moveCursorToNextLine() {
        for (int i = this.cursor; i < this.elements.size(); i++) {
            if (this.elements.get(i).type == ElementType.NEW_LINE) {
                this.cursor = i + 1;
                break;
            }
        }

        return this;
    }

    public TooltipBuilder lockCursor() {
        this.cursorLocked = true;
        return this;
    }

    public TooltipBuilder unlockCursor() {
        this.cursorLocked = false;
        return this;
    }

    // ---------------------------------------------------------------------
    // Build
    // ---------------------------------------------------------------------

    public TooltipData build(ItemStack stack, EntityPlayer player, boolean advanced) {
        return buildInternal(stack, player, null, advanced);
    }

    public void build(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (tooltip == null) return;
        TooltipData data = buildInternal(stack, player, tooltip, advanced);
        tooltip.clear();
        tooltip.addAll(data.getTextLines());
    }

    private TooltipData buildInternal(ItemStack stack, EntityPlayer player, List<String> originalLines,
        boolean advanced) {
        this.stack = stack;
        this.player = player;
        this.advanced = advanced;

        List<TooltipElement> previousElements = this.elements;
        int previousCursor = this.cursor;
        boolean previousCursorLocked = this.cursorLocked;

        this.elements = new ArrayList<>();

        if (originalLines != null) {
            for (String line : originalLines) {
                this.elements.add(new TooltipElement(ElementType.TEXT, line));
            }
        }

        this.elements.addAll(this.template);

        this.cursor = this.elements.size();
        this.cursorLocked = false;

        try {
            if (this.headerEnabled) {
                this.replace(0, this.addHeader());
            }

            for (TooltipProvider provider : this.providers) {
                provider.accept(this);
            }

            return new TooltipData(this.elements);
        } finally {
            this.elements = previousElements;
            this.cursor = Math.min(previousCursor, this.elements.size());
            this.cursorLocked = previousCursorLocked;
        }
    }

    // Header
    private boolean headerEnabled = true;
    private BiConsumer<HeaderTooltipLine, TooltipBuilder> headerConsumer = (header, builder) -> {};

    public TooltipBuilder header(BiConsumer<HeaderTooltipLine, TooltipBuilder> consumer) {
        this.headerConsumer = Objects.requireNonNull(consumer, "consumer");
        this.headerEnabled = true;
        return this;
    }

    public TooltipBuilder disableHeader() {
        this.headerEnabled = false;
        return this;
    }

    public TooltipBuilder enableHeader() {
        this.headerEnabled = true;
        return this;
    }

    public TooltipBuilder headerEnabled(boolean enabled) {
        this.headerEnabled = enabled;
        return this;
    }

    private String addHeader() {
        String title = "";

        if (!this.elements.isEmpty()) {
            TooltipElement first = this.elements.getFirst();
            if (first.getType() == ElementType.TEXT) {
                title = first.getText();
            }
        }

        HeaderTooltipLine header = new HeaderTooltipLine(this.stack, title);
        this.headerConsumer.accept(header, this);

        return TooltipLineHandler.register(header);
    }
}


package ruiseki.okcore.client.gui.tooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.helper.LangHelpers;

public final class TooltipBuilder {

    @FunctionalInterface
    public interface TooltipProvider {

        void accept(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced);
    }

    private final List<TooltipProvider> lines = new ArrayList<>();

    public TooltipBuilder addLine(String key, Object... params) {
        return this.addLine((stack, player, list, advanced) -> addText(list, LangHelpers.localize(key, params)));
    }

    public TooltipBuilder addLine(TooltipProvider provider) {
        this.lines.add(Objects.requireNonNull(provider, "provider"));
        return this;
    }

    public TooltipBuilder addLines(String... keys) {
        if (keys == null) return this;
        for (String key : keys) {
            this.addLine(key);
        }
        return this;
    }

    public TooltipBuilder addNewLine() {
        return this.addLine((stack, player, list, advanced) -> list.add(""));
    }

    public TooltipBuilder clear() {
        this.lines.clear();
        return this;
    }

    public TooltipBuilder replace(String regex, String key, Object... params) {
        return this.replace(regex, (stack, player, list, advanced) -> addText(list, LangHelpers.localize(key, params)));
    }

    public TooltipBuilder replace(String regex, TooltipProvider provider) {
        Pattern pattern = Pattern.compile(Objects.requireNonNull(regex, "regex"));
        Objects.requireNonNull(provider, "provider");
        this.lines.replaceAll(original -> (stack, player, list, advanced) -> {
            List<String> temporary = new ArrayList<>();
            original.accept(stack, player, temporary, advanced);
            for (String text : temporary) {
                if (text == null) continue;
                if (pattern.matcher(text)
                    .find()) {
                    provider.accept(stack, player, list, advanced);
                } else {
                    list.add(text);
                }
            }
        });
        return this;
    }

    public void build(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        for (TooltipProvider line : this.lines) {
            line.accept(stack, player, tooltip, advanced);
        }
    }

    private static void addText(List<String> list, String text) {
        if (text != null) {
            list.add(text);
        }
    }
}

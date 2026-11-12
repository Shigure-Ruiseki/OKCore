
package ruiseki.okcore.client.gui.tooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.helper.LangHelpers;

public class TooltipBuilder {

    @FunctionalInterface
    public interface TooltipProvider {

        void accept(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced);
    }

    private final List<TooltipProvider> lines = new ArrayList<>();

    public TooltipBuilder addLine(String key, Object... params) {
        return addLine((stack, player, list, advanced) -> list.add(LangHelpers.localize(key, params)));
    }

    public TooltipBuilder addLine(Function<ItemStack, String> provider) {
        return addLine((stack, player, list, advanced) -> addText(list, provider.apply(stack)));
    }

    public TooltipBuilder addLine(TooltipProvider provider) {
        lines.add(provider);
        return this;
    }

    public TooltipBuilder addNewLine() {
        return addLine((stack, player, list, advanced) -> list.add(""));
    }

    public TooltipBuilder clear() {
        lines.clear();
        return this;
    }

    public TooltipBuilder replace(String regex, String key, Object... params) {
        return replace(regex, (stack, player, list, advanced) -> LangHelpers.localize(key, params));
    }

    public TooltipBuilder replace(String regex, Function<ItemStack, String> provider) {
        return replace(regex, (stack, player, list, advanced) -> provider.apply(stack));
    }

    public TooltipBuilder replace(String regex, TooltipProvider provider) {
        Pattern pattern = Pattern.compile(regex);
        lines.replaceAll(original -> (stack, player, list, advanced) -> {
            List<String> temporary = new ArrayList<>();
            original.accept(stack, player, temporary, advanced);
            for (String text : temporary) {
                if (text != null && pattern.matcher(text)
                    .find()) {
                    provider.accept(stack, player, list, advanced);
                } else if (text != null) {
                    list.add(text);
                }
            }
        });
        return this;
    }

    public void build(ItemStack stack, EntityPlayer player, boolean advanced, List<String> tooltip) {
        for (TooltipProvider line : lines) {
            line.accept(stack, player, tooltip, advanced);
        }
    }

    private static void addText(List<String> list, String text) {
        if (text != null) {
            list.add(text);
        }
    }
}

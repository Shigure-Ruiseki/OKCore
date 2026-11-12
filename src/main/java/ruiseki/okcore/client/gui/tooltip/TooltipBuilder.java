
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

        String apply(ItemStack stack, EntityPlayer player, boolean advanced);
    }

    private final List<TooltipProvider> lines = new ArrayList<>();

    public TooltipBuilder addLine(String key, Object... params) {
        return addLine((stack, player, advanced) -> LangHelpers.localize(key, params));
    }

    public TooltipBuilder addLine(Function<ItemStack, String> provider) {
        return addLine((stack, player, advanced) -> provider.apply(stack));
    }

    public TooltipBuilder addLine(TooltipProvider provider) {
        lines.add(provider);
        return this;
    }

    public TooltipBuilder addNewLine() {
        return addLine((stack, player, advanced) -> "");
    }

    public TooltipBuilder clear() {
        lines.clear();
        return this;
    }

    public TooltipBuilder replace(String regex, String key, Object... params) {
        return replace(regex, (stack, player, advanced) -> LangHelpers.localize(key, params));
    }

    public TooltipBuilder replace(String regex, Function<ItemStack, String> provider) {
        return replace(regex, (stack, player, advanced) -> provider.apply(stack));
    }

    public TooltipBuilder replace(String regex, TooltipProvider provider) {
        Pattern pattern = Pattern.compile(regex);
        lines.replaceAll(tooltipProvider -> (stack, player, advanced) -> {
            String text = tooltipProvider.apply(stack, player, advanced);
            return text != null && pattern.matcher(text)
                .find() ? provider.apply(stack, player, advanced) : text;
        });
        return this;
    }

    public void build(ItemStack stack, EntityPlayer player, boolean advanced, List<String> tooltip) {
        for (TooltipProvider line : lines) {
            String text = line.apply(stack, player, advanced);
            if (text != null) {
                tooltip.add(text);
            }
        }
    }
}

package ruiseki.okcore.command.argument;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import net.minecraft.command.ICommandSender;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

/**
 * An argument type for a certain enum.
 * 
 * @author rubensworks
 */
public class ArgumentTypeEnum<T extends Enum<T>> implements ArgumentType<T> {

    private final Class<T> enumClass;

    public ArgumentTypeEnum(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        try {
            return Enum.valueOf(
                this.enumClass,
                reader.readString()
                    .toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new SimpleCommandExceptionType(() -> "Unknown value").create();
        }
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.stream(this.enumClass.getEnumConstants())
            .map(Enum::name)
            .map(name -> name.toLowerCase(Locale.ENGLISH))
            .collect(Collectors.toList());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining()
            .toLowerCase(Locale.ENGLISH);
        for (T enumValue : this.enumClass.getEnumConstants()) {
            String name = enumValue.name()
                .toLowerCase(Locale.ENGLISH);
            if (name.startsWith(remaining)) {
                builder.suggest(name);
            }
        }
        return builder.buildFuture();
    }

    public static <T extends Enum<T>> T getValue(CommandContext<ICommandSender> context, String name,
        Class<T> enumClass) {
        return context.getArgument(name, enumClass);
    }

}

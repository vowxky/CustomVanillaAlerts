package vowxky.customvanillaalerts.command.suggestion;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

import java.util.Arrays;
import java.util.List;

public class SugestionWordTypes implements SuggestionProvider<ServerCommandSource> {
    private final Type suggestionType;

    public enum Type {
        COLORS, STYLES, VARIABLES
    }

    public SugestionWordTypes(Type suggestionType) {
        this.suggestionType = suggestionType;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        switch (suggestionType) {
            case COLORS:
                return suggestColors(builder);
            case STYLES:
                return suggestStyles(builder);
            case VARIABLES:
                return suggestVariables(builder, context);
            default:
                return Suggestions.empty();
        }
    }

    private CompletableFuture<Suggestions> suggestColors(SuggestionsBuilder builder) {
        List<String> colorSuggestions = Arrays.asList(
                "black", "dark_blue", "dark_green", "dark_aqua",
                "dark_red", "dark_purple", "gold", "gray",
                "dark_gray", "blue", "green", "aqua",
                "red", "light_purple", "yellow", "white"
        );

        return suggestMatchingStrings(builder, colorSuggestions);
    }

    private CompletableFuture<Suggestions> suggestStyles(SuggestionsBuilder builder) {
        List<String> styleSuggestions = Arrays.asList(
                "bold", "italic", "underline", "strikethrough", "obfuscated", "none"
        );

        return suggestMatchingStrings(builder, styleSuggestions);
    }

    private CompletableFuture<Suggestions> suggestMatchingStrings(SuggestionsBuilder builder, List<String> suggestions) {
        String remaining = builder.getRemaining().toLowerCase();

        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(remaining)) {
                builder.suggest(suggestion);
            }
        }

        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestVariables(SuggestionsBuilder builder, CommandContext<ServerCommandSource> context) {
        String messageType = StringArgumentType.getString(context, "messageType");

        if ("death".equalsIgnoreCase(messageType)) {
            return suggestMatchingStrings(builder, Arrays.asList("player", "reason"));
        }

        return suggestMatchingStrings(builder, List.of("player"));
    }
}

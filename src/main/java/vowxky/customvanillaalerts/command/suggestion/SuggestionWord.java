package vowxky.customvanillaalerts.command.suggestion;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import vowxky.customvanillaalerts.CustomVanillaAlerts;
import vowxky.customvanillaalerts.config.Config;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SuggestionWord implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        String messageType = StringArgumentType.getString(context, "messageType");
        String messageId = StringArgumentType.getString(context, "messageId");

        Config config = CustomVanillaAlerts.getConfig();

        List<String> words = config.getWordsByTypeAndId(messageType.toLowerCase(), messageId);

        String input = builder.getRemaining().toLowerCase();
        List<String> matchingWords = words.stream()
                .filter(word -> word.toLowerCase().startsWith(input))
                .map(word -> word.replace("%", ""))
                .toList();

        matchingWords.forEach(builder::suggest);

        return builder.buildFuture();
    }
}

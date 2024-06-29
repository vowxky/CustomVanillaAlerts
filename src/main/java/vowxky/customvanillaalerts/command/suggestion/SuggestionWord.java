package vowxky.customvanillaalerts.command.suggestion;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import vowxky.customvanillaalerts.config.Config;
import vowxky.customvanillaalerts.config.Word;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;


public class SuggestionWord implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        String messageType = StringArgumentType.getString(context, "messageType");
        String messageId = StringArgumentType.getString(context, "messageId");

        List<Word> words = Config.getInstance().getWordsByTypeAndId(messageType, messageId);

        IntStream.range(0, words.size())
                .forEach(i -> builder.suggest(String.valueOf(i)));

        return builder.buildFuture();
    }
}
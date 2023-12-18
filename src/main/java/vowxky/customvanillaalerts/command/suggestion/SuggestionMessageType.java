package vowxky.customvanillaalerts.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SuggestionMessageType implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        List<String> messageTypes = new ArrayList<>();
        messageTypes.add("death");
        messageTypes.add("disconnect");
        messageTypes.add("join");

        messageTypes.forEach(builder::suggest);

        return builder.buildFuture();
    }
}

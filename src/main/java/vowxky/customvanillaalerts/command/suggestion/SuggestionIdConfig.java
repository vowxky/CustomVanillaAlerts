package vowxky.customvanillaalerts.command.suggestion;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import vowxky.customvanillaalerts.CustomVanillaAlerts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SuggestionIdConfig implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String messageType = StringArgumentType.getString(context , "messageType");

        List<String> messageIds = getMessageIdsForType(messageType);
        messageIds.forEach(builder::suggest);

        return builder.buildFuture();
    }

    private List<String> getMessageIdsForType(String messageType) {
        List<String> messageIds = new ArrayList<>();

        switch (messageType.toLowerCase()) {
            case "death":
                messageIds.addAll(CustomVanillaAlerts.getConfig().getDeathMessageIds());
                break;
            case "disconnect":
                messageIds.addAll(CustomVanillaAlerts.getConfig().getDisconnectMessageIds());
                break;
            case "join":
                messageIds.addAll(CustomVanillaAlerts.getConfig().getJoinMessageIds());
                break;
            default:
                break;
        }

        return messageIds;
    }
}
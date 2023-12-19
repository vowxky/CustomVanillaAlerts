package vowxky.customvanillaalerts.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import vowxky.customvanillaalerts.CustomVanillaAlerts;
import vowxky.customvanillaalerts.command.suggestion.SugestionWordTypes;
import vowxky.customvanillaalerts.command.suggestion.SuggestionIdConfig;
import vowxky.customvanillaalerts.command.suggestion.SuggestionMessageType;
import vowxky.customvanillaalerts.command.suggestion.SuggestionWord;
import vowxky.customvanillaalerts.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class CustomVanillaAlertsCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("cva").requires(source -> source.hasPermissionLevel(2))

                        .then(CommandManager.literal("config")
                                .then(CommandManager.literal("reload")
                                        .executes(CustomVanillaAlertsCommands::reload)
                                )

                                .then(CommandManager.literal("restore")
                                        .executes(CustomVanillaAlertsCommands::restoreConfig)
                                )
                        )

                        .then(CommandManager.literal("changeVisibility")
                                .then(CommandManager.argument("visibility", BoolArgumentType.bool())
                                        .then(CommandManager.literal("DeathMessages")
                                                .executes(context -> changeVisibility(context, "death"))
                                        )
                                        .then(CommandManager.literal("DisconnectMessages")
                                                .executes(context -> changeVisibility(context, "disconnect"))
                                        )
                                        .then(CommandManager.literal("JoinMessages")
                                                .executes(context -> changeVisibility(context, "join"))
                                        )
                                )
                        )

                        .then(CommandManager.literal("words")
                                .then(CommandManager.literal("addWords")
                                        .then(CommandManager.argument("messageType", StringArgumentType.string())
                                                .suggests(new SuggestionMessageType())
                                                .then(CommandManager.argument("messageId", StringArgumentType.string())
                                                        .suggests(new SuggestionIdConfig())
                                                        .then(CommandManager.argument("content", StringArgumentType.string())
                                                                .suggests(new SugestionWordTypes(SugestionWordTypes.Type.VARIABLES))
                                                                .then(CommandManager.argument("color", StringArgumentType.string())
                                                                        .suggests(new SugestionWordTypes(SugestionWordTypes.Type.COLORS))
                                                                        .then(CommandManager.argument("style", StringArgumentType.string())
                                                                                .suggests(new SugestionWordTypes(SugestionWordTypes.Type.STYLES))
                                                                                .executes(CustomVanillaAlertsCommands::addWords)
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(CommandManager.literal("removeWords")
                                        .then(CommandManager.argument("messageType", StringArgumentType.string())
                                                .suggests(new SuggestionMessageType())
                                                .then(CommandManager.argument("messageId", StringArgumentType.string())
                                                        .suggests(new SuggestionIdConfig())
                                                        .then(CommandManager.argument("contentWord", StringArgumentType.string())
                                                                .suggests(new SuggestionWord())
                                                                .executes(CustomVanillaAlertsCommands::removeWords)
                                                        )
                                                )
                                        )
                                )
                        )

                        .then(CommandManager.literal("message")
                                .then(CommandManager.literal("createMessage")
                                        .then(CommandManager.argument("messageType", StringArgumentType.string())
                                                .suggests(new SuggestionMessageType())
                                                .then(CommandManager.argument("messageId", StringArgumentType.string())
                                                        .executes(CustomVanillaAlertsCommands::createMessage)
                                                )
                                        )
                                )
                                .then(CommandManager.literal("deleteMessage")
                                        .then(CommandManager.argument("messageType", StringArgumentType.string())
                                                .suggests(new SuggestionMessageType())
                                                .then(CommandManager.argument("messageId", StringArgumentType.string())
                                                        .suggests(new SuggestionIdConfig())
                                                        .executes(CustomVanillaAlertsCommands::deleteMessage)
                                                )
                                        )
                                )
                        )
        );
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        CustomVanillaAlerts.getConfig().load();
        context.getSource().sendFeedback(Text.of("The config was reloaded") , false);
        return 1;
    }

    private static int restoreConfig(CommandContext<ServerCommandSource> context) {
        Config config = CustomVanillaAlerts.getConfig();
        config.setDefaultConfigValues();
        config.load();
        context.getSource().sendFeedback(Text.of("Config restored to default values."), true);

        return 1;
    }

    private static int changeVisibility(CommandContext<ServerCommandSource> context, String configKey) {
        boolean currentValue = BoolArgumentType.getBool(context , "visibility");
        switch (configKey.toLowerCase()) {
            case "death":
                CustomVanillaAlerts.getConfig().setEnabledDeathMessages(currentValue);
                break;
            case "disconnect":
                CustomVanillaAlerts.getConfig().setEnabledDisconnectMessages(currentValue);
                break;
            case "join":
                CustomVanillaAlerts.getConfig().setEnabledJoinMessages(currentValue);
                break;
            default:
                context.getSource().sendError(Text.of("Invalid config key: " + configKey));
                return 0;
        }
        context.getSource().sendFeedback(Text.of("The value for " + configKey + " has changed to " + currentValue) , false);
        return 1;
    }

    private static int createMessage(CommandContext<ServerCommandSource> context) {
        String messageType = StringArgumentType.getString(context, "messageType");
        String messageId = StringArgumentType.getString(context, "messageId");

        switch (messageType.toLowerCase()) {
            case "death":
                CustomVanillaAlerts.getConfig().createDeathMessage(messageId, new ArrayList<>());
                break;
            case "disconnect":
                CustomVanillaAlerts.getConfig().createDisconnectMessage(messageId, new ArrayList<>());
                break;
            case "join":
                CustomVanillaAlerts.getConfig().createJoinMessage(messageId, new ArrayList<>());
                break;
            default:
                context.getSource().sendError(Text.of("Invalid message type: " + messageType));
                return 0;
        }
        context.getSource().sendFeedback(Text.of("Message created with ID " + messageId), false);

        return 1;
    }

    private static int addWords(CommandContext<ServerCommandSource> context) {
        String messageType = StringArgumentType.getString(context, "messageType");
        String messageId = StringArgumentType.getString(context, "messageId");
        String content = StringArgumentType.getString(context, "content");
        String color = StringArgumentType.getString(context, "color");
        String style = StringArgumentType.getString(context, "style");

        if (messageType.equalsIgnoreCase("death")) {
            content = content.replace("reason", "%reason%");
        }

        content = content.replace("player", "%player%");

        List<String> styles = null;

        if (!style.equalsIgnoreCase("none")) {
            styles = Collections.singletonList(style);
        }

        Map<String, Object> word = CustomVanillaAlerts.getConfig().createWord(content, color, styles);

        switch (messageType.toLowerCase()) {
            case "death":
                CustomVanillaAlerts.getConfig().addWordToDeathMessage(messageId, word);
                break;
            case "disconnect":
                CustomVanillaAlerts.getConfig().addWordToDisconnectMessage(messageId, word);
                break;
            case "join":
                CustomVanillaAlerts.getConfig().addWordToJoinMessage(messageId, word);
                break;
            default:
                context.getSource().sendError(Text.of("Invalid message type: " + messageType));
                return 0;
        }

        context.getSource().sendFeedback(Text.of("Word added to message " + messageId), false);
        return 1;
    }

    private static int deleteMessage(CommandContext<ServerCommandSource> context) {
        String messageType = StringArgumentType.getString(context, "messageType");
        String messageId = StringArgumentType.getString(context, "messageId");

        switch (messageType.toLowerCase()) {
            case "death":
                CustomVanillaAlerts.getConfig().deleteDeathMessage(messageId);
                break;
            case "disconnect":
                CustomVanillaAlerts.getConfig().deleteDisconnectMessage(messageId);
                break;
            case "join":
                CustomVanillaAlerts.getConfig().deleteJoinMessage(messageId);
                break;
            default:
                context.getSource().sendError(Text.of("Invalid message type: " + messageType));
                return 0;
        }

        context.getSource().sendFeedback(Text.of("Message deleted with ID " + messageId), false);
        return 1;
    }

    private static int removeWords(CommandContext<ServerCommandSource> context) {
        String messageType = StringArgumentType.getString(context, "messageType");
        String messageId = StringArgumentType.getString(context, "messageId");
        String contentWord = StringArgumentType.getString(context, "contentWord");

        if (messageType.equalsIgnoreCase("death")) {
            contentWord = contentWord.replace("reason", "%reason%");
        }

        contentWord = contentWord.replace("player", "%player%");

        switch (messageType.toLowerCase()) {
            case "death":
                CustomVanillaAlerts.getConfig().removeWordFromDeathMessage(messageId, contentWord);
                break;
            case "disconnect":
                CustomVanillaAlerts.getConfig().removeWordFromDisconnectMessage(messageId, contentWord);
                break;
            case "join":
                CustomVanillaAlerts.getConfig().removeWordFromJoinMessage(messageId, contentWord);
                break;
            default:
                context.getSource().sendError(Text.of("Invalid message type: " + messageType));
                return 0;
        }

        context.getSource().sendFeedback(Text.of("Word '" + contentWord + "' removed from message " + messageId), false);
        return 1;
    }
}

package vowxky.customvanillaalerts.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.IntegerSuggestion;
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
                                        .then(CommandManager.literal("Death")
                                                .executes(context -> changeVisibility(context, "death"))
                                        )
                                        .then(CommandManager.literal("Disconnect")
                                                .executes(context -> changeVisibility(context, "disconnect"))
                                        )
                                        .then(CommandManager.literal("Join")
                                                .executes(context -> changeVisibility(context, "join"))
                                        )
                                        .then(CommandManager.literal("Advancement")
                                                .executes(context -> changeVisibility(context, "advancements")))
                                )
                        )

                        .then(CommandManager.literal("words")
                                .then(CommandManager.literal("add")
                                        .then(CommandManager.argument("messageType", StringArgumentType.string())
                                                .suggests(new SuggestionMessageType())
                                                .then(CommandManager.argument("messageId", StringArgumentType.string())
                                                        .suggests(new SuggestionIdConfig())
                                                        .then(CommandManager.argument("content", StringArgumentType.string())
                                                                .then(CommandManager.argument("color", StringArgumentType.string())
                                                                        .suggests(new SugestionWordTypes(SugestionWordTypes.Type.COLORS))
                                                                        .then(CommandManager.argument("style", StringArgumentType.string())
                                                                                .suggests(new SugestionWordTypes(SugestionWordTypes.Type.STYLES))
                                                                                .executes(CustomVanillaAlertsCommands::executeAddWords)
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )

                                .then(CommandManager.literal("remove")
                                        .then(CommandManager.argument("messageType", StringArgumentType.string())
                                                .suggests(new SuggestionMessageType())
                                                .then(CommandManager.argument("messageId", StringArgumentType.string())
                                                        .suggests(new SuggestionIdConfig())
                                                        .then(CommandManager.argument("wordIndex", IntegerArgumentType.integer())
                                                                .suggests(new SuggestionWord())
                                                                .executes(CustomVanillaAlertsCommands::executeRemoveWords)
                                                        )
                                                )
                                        )
                                )

                                .then(CommandManager.literal("edit")
                                        .then(CommandManager.argument("messageType", StringArgumentType.string())
                                                .suggests(new SuggestionMessageType())
                                                .then(CommandManager.argument("messageId", StringArgumentType.string())
                                                        .suggests(new SuggestionIdConfig())
                                                        .then(CommandManager.argument("oldWordIndex", IntegerArgumentType.integer())
                                                                .suggests(new SuggestionWord())
                                                                .then(CommandManager.argument("newContent", StringArgumentType.string())
                                                                        .then(CommandManager.argument("newColor", StringArgumentType.string())
                                                                                .suggests(new SugestionWordTypes(SugestionWordTypes.Type.COLORS))
                                                                                .then(CommandManager.argument("newStyle", StringArgumentType.string())
                                                                                        .suggests(new SugestionWordTypes(SugestionWordTypes.Type.STYLES))
                                                                                        .executes(CustomVanillaAlertsCommands::executeEditWords)
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )

                        .then(CommandManager.literal("message")
                                .then(CommandManager.literal("create")
                                        .then(CommandManager.argument("messageType", StringArgumentType.string())
                                                .suggests(new SuggestionMessageType())
                                                .then(CommandManager.argument("messageId", StringArgumentType.string())
                                                        .executes(CustomVanillaAlertsCommands::executeCreateMessage)
                                                )
                                        )
                                )
                                .then(CommandManager.literal("delete")
                                        .then(CommandManager.argument("messageType", StringArgumentType.string())
                                                .suggests(new SuggestionMessageType())
                                                .then(CommandManager.argument("messageId", StringArgumentType.string())
                                                        .suggests(new SuggestionIdConfig())
                                                        .executes(CustomVanillaAlertsCommands::executeDeleteMessage)
                                                )
                                        )
                                )
                        )
        );
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        Config.getInstance().loadConfig();
        context.getSource().sendFeedback(Text.of("The config was reloaded") , false);
        return 1;
    }

    private static int restoreConfig(CommandContext<ServerCommandSource> context) {
        Config.getInstance().restoreDefaultConfig();
        context.getSource().sendFeedback(Text.of("Config restored to default values."), true);
        return 1;
    }

    private static int changeVisibility(CommandContext<ServerCommandSource> context, String configKey) {
        boolean currentValue = BoolArgumentType.getBool(context , "visibility");
        Config.getInstance().setEnabled(configKey , currentValue);
        context.getSource().sendFeedback(Text.of("The value for " + configKey + " has changed to " + currentValue) , false);
        return 1;
    }

    private static int executeAddWords(CommandContext<ServerCommandSource> context) {
        String messageType = StringArgumentType.getString(context, "messageType");
        String messageId = StringArgumentType.getString(context, "messageId");
        String content = StringArgumentType.getString(context, "content");
        String color = StringArgumentType.getString(context, "color");
        String style = StringArgumentType.getString(context, "style");

        List<String> styles = null;

        content = content.replace("player", "%player%");

        if (messageType.equalsIgnoreCase("death")) {
            content = content.replace("reason", "%reason%");
        } else if (messageType.equalsIgnoreCase("advancement")) {
            content = content.replace("advancement", "%advancement%");
        }

        if (!style.equalsIgnoreCase("none")) {
            styles = Collections.singletonList(style);
        }

        Config.getInstance().addWordToMessageList(messageType, messageId, content, color, styles);

        context.getSource().sendFeedback(Text.of("Word added to message " + messageId + " for type " + messageType), false);
        return 1;
    }

    private static int executeRemoveWords(CommandContext<ServerCommandSource> context) {
        String messageType = StringArgumentType.getString(context, "messageType");
        String messageId = StringArgumentType.getString(context, "messageId");

        int wordIndex = IntegerArgumentType.getInteger(context, "wordIndex");

        Config.getInstance().removeWord(messageType, messageId, wordIndex);
        context.getSource().sendFeedback(Text.of("Word removed from message " + messageId + " for type " + messageType), false);

        return 1;
    }

    private static int executeCreateMessage(CommandContext<ServerCommandSource> context) {
        String messageType = StringArgumentType.getString(context, "messageType");
        String messageId = StringArgumentType.getString(context, "messageId");

        Config.getInstance().addMessage(messageType, messageId);

        context.getSource().sendFeedback(Text.of("Message created with ID " + messageId + " for type " + messageType), false);
        return 1;
    }

    private static int executeEditWords(CommandContext<ServerCommandSource> context) {
        String messageType = StringArgumentType.getString(context, "messageType");
        String messageId = StringArgumentType.getString(context, "messageId");
        int oldWordIndex = IntegerArgumentType.getInteger(context, "oldWordIndex");
        String newContent = StringArgumentType.getString(context, "newContent");
        String newColor = StringArgumentType.getString(context, "newColor");
        String newStyle = StringArgumentType.getString(context, "newStyle");

        newContent = newContent.replace("player", "%player%");

        if (messageType.equalsIgnoreCase("death")) {
            newContent = newContent.replace("reason", "%reason%");
        } else if (messageType.equalsIgnoreCase("advancement")) {
            newContent = newContent.replace("advancement", "%advancement%");
        }

        List<String> styles = null;

        if (!newStyle.equalsIgnoreCase("none")) {
            styles = Collections.singletonList(newStyle);
        }

        Config.getInstance().editWord(messageType, messageId, oldWordIndex, newContent, newColor, styles);

        context.getSource().sendFeedback(Text.of("Word edited in message " + messageId + " for type " + messageType), false);
        return 1;
    }
    private static int executeDeleteMessage(CommandContext<ServerCommandSource> context) {
        String messageType = StringArgumentType.getString(context, "messageType");
        String messageId = StringArgumentType.getString(context, "messageId");

        Config.getInstance().deleteMessage(messageType, messageId);

        context.getSource().sendFeedback(Text.of("Message deleted with ID " + messageId + " for type " + messageType), false);
        return 1;
    }
}

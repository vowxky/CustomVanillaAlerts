package vowxky.customvanillaalerts.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;
import java.util.Random;


public class MessageBuilder {
    public static MutableText buildMessage(Map<String, Object> message, String player) {
        return buildMessage(message, player, null, null);
    }

    public static MutableText buildMessage(Map<String, Object> message, String player, String reason, Text advancement) {
        MutableText messageText = (MutableText) Text.of("");

        List<Map<String, Object>> words = (List<Map<String, Object>>) message.get("words");

        boolean firstWord = true;
        for (Map<String, Object> word : words) {
            String content = (String) word.get("content");
            String color = (String) word.get("color");
            List<String> style = (List<String>) word.get("style");

            content = content.replace("%player%", player);
            if (reason != null) {
                content = content.replace("%reason%", reason);
            }

            MutableText wordText;

            if (content.contains("%advancement%") && advancement != null) {
                wordText = MutableText.of(Text.of(content.replace("%advancement%", "").trim()).getContent()).append(advancement);
            } else {
                wordText = (MutableText) Text.of(content);
            }

            if (color != null) {
                wordText = wordText.formatted(Formatting.byName(color));
            }

            if (style != null) {
                for (String styleName : style) {
                    wordText = applyStyle(wordText, styleName);
                }
            }

            if (!firstWord) {
                messageText.append(Text.of(" "));
            } else {
                firstWord = false;
            }
            messageText.append(wordText);
        }

        return messageText;
    }

    private static MutableText applyStyle(MutableText text, String styleName) {
        return switch (styleName) {
            case "bold" -> text.styled(style -> style.withBold(true));
            case "italic" -> text.styled(style -> style.withItalic(true));
            case "underline" -> text.styled(style -> style.withUnderline(true));
            case "strikethrough" -> text.styled(style -> style.withStrikethrough(true));
            case "obfuscated" -> text.styled(style -> style.withObfuscated(true));
            default -> text;
        };
    }

    public static Map<String, Object> getRandomMessage(List<Map<String, Object>> messages) {
        Random random = new Random();
        int index = random.nextInt(messages.size());
        return messages.get(index);
    }
}
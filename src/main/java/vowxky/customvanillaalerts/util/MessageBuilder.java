package vowxky.customvanillaalerts.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class MessageBuilder {
    public static MutableText buildMessage(Map<String, Object> message, String player) {
        boolean firstWord = true;
        MutableText messageText = Text.translatable("damage.buildmessage");

        List<Map<String, Object>> words = (List<Map<String, Object>>) message.get("words");

        for (Map<String, Object> word : words) {
            String content = (String) word.get("content");
            String color = (String) word.get("color");
            List<String> style = (List<String>) word.get("style");
            content = content.replace("%player%", player);

            MutableText wordText = (MutableText) Text.of(content);

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

    public static MutableText buildMessage(Map<String, Object> message, String player, String reason) {
        boolean firstWord = true;
        MutableText messageText = Text.translatable("damage.buildmessage");

        List<Map<String, Object>> words = (List<Map<String, Object>>) message.get("words");

        for (Map<String, Object> word : words) {
            String content = (String) word.get("content");
            String color = (String) word.get("color");
            List<String> style = (List<String>) word.get("style");
            content = content.replace("%player%", player);
            content = content.replace("%reason%", reason);

            MutableText wordText = (MutableText) Text.of(content);

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
        switch (styleName) {
            case "bold":
                return text.styled(style -> style.withBold(true));
            case "italic":
                return text.styled(style -> style.withItalic(true));
            case "underline":
                return text.styled(style -> style.withUnderline(true));
            case "strikethrough":
                return text.styled(style -> style.withStrikethrough(true));
            case "obfuscated":
                return text.styled(style -> style.withObfuscated(true));
            default:
                return text;
        }
    }

    public static Map<String, Object> getRandomMessage(List<Map<String, Object>> messages) {
        Random random = new Random();
        int index = random.nextInt(messages.size());
        return messages.get(index);
    }
}

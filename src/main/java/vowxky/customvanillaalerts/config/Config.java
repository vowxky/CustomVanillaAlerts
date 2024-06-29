package vowxky.customvanillaalerts.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import vowxky.customvanillaalerts.CustomVanillaAlerts;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Config {

    private boolean initialized = false;
    private File configFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private ConfigData data;

    private Config() {
    }

    private static class SingletonHolder {
        private static final Config INSTANCE = new Config();
    }

    public static Config getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void init() {
        if (initialized) return;
        initialized = true;
        configFile = FabricLoader.getInstance().getConfigDir().resolve("cva.json").toFile();
        if (!configFile.exists()) {
            data = new ConfigData();
            saveConfig();
            return;
        }
        loadConfig();
    }

    public void loadConfig() {
        try (FileReader reader = new FileReader(configFile)) {
            data = gson.fromJson(reader, ConfigData.class);
        } catch (IOException e) {
            CustomVanillaAlerts.LOGGER.error("Error loading the config " + e);
            data = new ConfigData();
        }
    }

    public void saveConfig() {
        try (FileWriter fileWriter = new FileWriter(configFile)) {
            gson.toJson(data, fileWriter);
        } catch (IOException e) {
            CustomVanillaAlerts.LOGGER.error("Error saving the config " + e);
        }
    }

    public void setEnabled(String key, boolean value) {
        data.setEnabled(key, value);
        saveConfig();
    }

    public boolean isEnabled(String key) {
        return data.isEnabled(key);
    }

    public void editWord(String messageType, String messageId, int wordIndex, String newContent, String newColor, List<String> newStyle) {
        data.editWord(messageType, messageId, wordIndex, newContent, newColor, newStyle);
        saveConfig();
    }

    public void addWordToMessageList(String messageType, String messageId, String content, String color, List<String> style) {
        data.addWordToMessageList(messageType, messageId, content, color, style);
        saveConfig();
    }

    public void addMessage(String messageType, String messageId) {
        data.addMessage(messageType, messageId);
        saveConfig();
    }

    public void deleteMessage(String messageType, String messageId) {
        data.deleteMessage(messageType, messageId);
        saveConfig();
    }

    public void removeWord(String messageType, String messageId, int wordIndex) {
        data.removeWord(messageType, messageId, wordIndex);
        saveConfig();
    }

    public List<Word> getWordsByTypeAndId(String messageType, String messageId) {
        loadConfig();
        return data.getWordsByTypeAndId(messageType, messageId);
    }

    public List<String> getMessageIdsByType(String messageType) {
        loadConfig();
        return data.getMessageIdsByType(messageType);
    }

    public List<Map<String, Object>> getMessagesByType(String messageType) {
        loadConfig();
        return data.getMessagesByType(messageType);
    }

    public void restoreDefaultConfig() {
        data = new ConfigData();
        saveConfig();
    }
}

class ConfigData {
    private final Map<String, Boolean> enabledMessages = new HashMap<>();
    private final Map<String, List<Message>> listMessages = new HashMap<>();

    public ConfigData() {
        setDefaultEnabledMessages();
        setDefaultMessages();
    }

    private void setDefaultEnabledMessages() {
        enabledMessages.put("death", true);
        enabledMessages.put("disconnect", true);
        enabledMessages.put("join", true);
        enabledMessages.put("advancements", true);
    }

    private void setDefaultMessages() {
        listMessages.put("disconnect", Arrays.asList(
                new Message("disconnectMessage", Arrays.asList(
                        new Word("gray", null, "%player%"),
                        new Word(null, null, "has"),
                        new Word(null, null, "lost"),
                        new Word(null, null, "connection...")))));

        listMessages.put("death", Arrays.asList(
                new Message("deathMessage", Arrays.asList(
                        new Word("red", Collections.singletonList("bold"), "%player%"),
                        new Word(null, null, "has"),
                        new Word(null, null, "succumbed"),
                        new Word(null, null, "in"),
                        new Word(null, null, "the"),
                        new Word(null, null, "battle...")))));

        listMessages.put("advancement", Arrays.asList(
                new Message("advancementMessage", Arrays.asList(
                        new Word("green", null, "%player%"),
                        new Word(null, null, "got"),
                        new Word(null, null, "the"),
                        new Word(null, null, "advancement"),
                        new Word("aqua", Collections.singletonList("bold"), "%advancement%")))));

        listMessages.put("join", Arrays.asList(
                new Message("joinMessage", Arrays.asList(
                        new Word("green", null, "%player%"),
                        new Word(null, null, "has"),
                        new Word(null, null, "joined"),
                        new Word(null, null, "the"),
                        new Word(null, null, "game.")))));
    }

    public void setEnabled(String key, boolean value) {
        validateKey(key);
        enabledMessages.put(key, value);
    }

    public boolean isEnabled(String key) {
        return enabledMessages.getOrDefault(key, true);
    }

    public void editWord(String messageType, String messageId, int wordIndex, String newContent, String newColor, List<String> newStyles) {
        getMessageByTypeAndId(messageType, messageId)
                .ifPresent(message -> message.editWord(wordIndex, newContent, newColor, newStyles));
    }

    public void removeWord(String messageType, String messageId, int wordIndex) {
        getMessageByTypeAndId(messageType, messageId)
                .ifPresent(message -> {
                    List<Word> words = new ArrayList<>(message.getWords());
                    if (wordIndex >= 0 && wordIndex < words.size()) {
                        words.remove(wordIndex);
                        message.setWords(words);
                    } else {
                        System.out.println("Invalid word index: " + wordIndex);
                    }
                });
    }

    public void addWordToMessageList(String messageType, String messageId, String content, String color, List<String> style) {
        getMessageByTypeAndId(messageType, messageId)
                .ifPresent(message -> message.addWord(new Word(color, style, content)));
    }

    public void addMessage(String messageType, String messageId) {
        listMessages.computeIfAbsent(messageType, k -> new ArrayList<>()).add(new Message(messageId, new ArrayList<>()));
    }

    public void deleteMessage(String messageType, String messageId) {
        listMessages.computeIfPresent(messageType, (k, v) -> {
            v.removeIf(message -> message.getId().equals(messageId));
            return v;
        });
    }

    public List<Word> getWordsByTypeAndId(String messageType, String messageId) {
        return getMessageByTypeAndId(messageType, messageId)
                .map(Message::getWords)
                .orElse(Collections.emptyList());
    }

    public List<String> getMessageIdsByType(String messageType) {
        return listMessages.getOrDefault(messageType, Collections.emptyList())
                .stream()
                .map(Message::getId)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getMessagesByType(String messageType) {
        return listMessages.getOrDefault(messageType, Collections.emptyList())
                .stream()
                .map(Message::toMap)
                .collect(Collectors.toList());
    }

    private Optional<Message> getMessageByTypeAndId(String messageType, String messageId) {
        return listMessages.getOrDefault(messageType, Collections.emptyList())
                .stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst();
    }

    private void validateKey(String key) {
        if (!enabledMessages.containsKey(key)) {
            throw new IllegalArgumentException("Invalid key: " + key);
        }
    }
}
package vowxky.customvanillaalerts.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Config {
    private final String configFilePath;
    private boolean enabledDeathMessages = false;
    private boolean enabledDisconnectMessages = false;
    private boolean enabledJoinMessages = false;

    private List<Map<String, Object>> deathMessages = new ArrayList<>();
    private List<Map<String, Object>> disconnectMessages = new ArrayList<>();
    private List<Map<String, Object>> joinMessages = new ArrayList<>();

    public Config(String fileName, String modId) {
        File configDir = new File("config");
        configDir.mkdirs();

        File modDir = new File(configDir, modId);
        modDir.mkdirs();

        this.configFilePath = new File(configDir, modId + "/" + fileName).getAbsolutePath();
    }

    public void load() {
        File file = new File(configFilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                setDefaultConfigValues();
            } catch (IOException e) {
                throw new RuntimeException("Error creating JSON file", e);
            }
        } else {
            try (FileReader fileReader = new FileReader(file);
                 BufferedReader bufferedReader = new BufferedReader(fileReader)) {

                String line;
                StringBuilder jsonData = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    jsonData.append(line);
                }

                Gson gson = new Gson();
                try {
                    Map<String, Object> finalConfigMap = gson.fromJson(jsonData.toString(), Map.class);

                    if (finalConfigMap == null) {
                        throw new RuntimeException("The JSON file is empty or malformed.");
                    }

                    Map<String, Object> booleanConfigMap = (Map<String, Object>) finalConfigMap.getOrDefault("booleanConfig", new HashMap<>());
                    enabledDeathMessages = (Boolean) booleanConfigMap.getOrDefault("enabledDeathMessages", false);
                    enabledDisconnectMessages = (Boolean) booleanConfigMap.getOrDefault("enabledDisconnectMessages", false);
                    enabledJoinMessages = (Boolean) booleanConfigMap.getOrDefault("enabledJoinMessages", false);

                    Map<String, Object> listConfigMap = (Map<String, Object>) finalConfigMap.getOrDefault("listConfig", new HashMap<>());
                    deathMessages = (List<Map<String, Object>>) listConfigMap.getOrDefault("deathMessages", new ArrayList<>());
                    disconnectMessages = (List<Map<String, Object>>) listConfigMap.getOrDefault("disconnectMessages", new ArrayList<>());
                    joinMessages = (List<Map<String, Object>>) listConfigMap.getOrDefault("joinMessages", new ArrayList<>());

                } catch (Exception e) {
                    throw new RuntimeException("Error converting JSON to map", e);
                }


            } catch (IOException e) {
                throw new RuntimeException("Error reading JSON file", e);
            }
        }
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(configFilePath);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            Map<String, Object> booleanConfigMap = new HashMap<>();
            booleanConfigMap.put("enabledDeathMessages", enabledDeathMessages);
            booleanConfigMap.put("enabledDisconnectMessages", enabledDisconnectMessages);
            booleanConfigMap.put("enabledJoinMessages", enabledJoinMessages);

            Map<String, Object> listConfigMap = new HashMap<>();
            listConfigMap.put("deathMessages", deathMessages);
            listConfigMap.put("disconnectMessages", disconnectMessages);
            listConfigMap.put("joinMessages", joinMessages);

            Map<String, Object> finalConfigMap = new HashMap<>();
            finalConfigMap.put("booleanConfig", booleanConfigMap);
            finalConfigMap.put("listConfig", listConfigMap);

            String jsonData = gson.toJson(finalConfigMap);
            bufferedWriter.write(jsonData);

        } catch (IOException e) {
            throw new RuntimeException("Error writing to JSON file", e);
        }
    }

    public boolean isEnabledDeathMessages() {
        return enabledDeathMessages;
    }

    public void setEnabledDeathMessages(boolean enabledDeathMessages) {
        this.enabledDeathMessages = enabledDeathMessages;
        save();
    }

    public boolean isEnabledDisconnectMessages() {
        return enabledDisconnectMessages;
    }

    public void setEnabledDisconnectMessages(boolean enabledDisconnectMessages) {
        this.enabledDisconnectMessages = enabledDisconnectMessages;
        save();
    }

    public boolean isEnabledJoinMessages() {
        return enabledJoinMessages;
    }

    public void setEnabledJoinMessages(boolean enabledJoinMessages) {
        this.enabledJoinMessages = enabledJoinMessages;
        save();
    }

    public List<Map<String, Object>> getDeathMessages() {
        return deathMessages;
    }

    public List<Map<String, Object>> getDisconnectMessages() {
        return disconnectMessages;
    }

    public List<Map<String, Object>> getJoinMessages() {
        return joinMessages;
    }

    public Map<String, Object> createWord(String content, String color, List<String> style) {
        Map<String, Object> wordMap = new HashMap<>();
        wordMap.put("content", content);
        if (color != null) {
            wordMap.put("color", color);
        }
        if (style != null) {
            wordMap.put("style", style);
        }
        return wordMap;
    }

    public Map<String, Object> createMessage(String id, List<Map<String, Object>> words) {
        Map<String, Object> message = new HashMap<>();
        message.put("id", id);
        message.put("words", words);
        return message;
    }

    public void addWordToDeathMessage(String messageId, Map<String, Object> word) {
        addWordToMessage(deathMessages, messageId, word);
    }

    public void addWordToDisconnectMessage(String messageId, Map<String, Object> word) {
        addWordToMessage(disconnectMessages, messageId, word);
    }

    public void addWordToJoinMessage(String messageId, Map<String, Object> word) {
        addWordToMessage(joinMessages, messageId, word);
    }

    public void createDeathMessage(String messageId, List<Map<String, Object>> words) {
        createMessage(deathMessages, messageId, words);
    }

    public void createDisconnectMessage(String messageId, List<Map<String, Object>> words) {
        createMessage(disconnectMessages, messageId, words);
    }

    public void createJoinMessage(String messageId, List<Map<String, Object>> words) {
        createMessage(joinMessages, messageId, words);
    }

    private void addWordToMessage(List<Map<String, Object>> messages, String messageId, Map<String, Object> word) {
        for (Map<String, Object> message : messages) {
            if (messageId.equals(message.get("id"))) {
                List<Map<String, Object>> words = (List<Map<String, Object>>) message.get("words");
                words.add(word);
                save();
                return;
            }
        }
    }

    private void createMessage(List<Map<String, Object>> messages, String messageId, List<Map<String, Object>> words) {
        Map<String, Object> newMessage = createMessage(messageId, words);
        messages.add(newMessage);
        save();
    }

    public void removeWordFromDeathMessage(String messageId, String content) {
        removeWordFromMessage(deathMessages, messageId, content);
    }

    public void removeWordFromDisconnectMessage(String messageId, String content) {
        removeWordFromMessage(disconnectMessages, messageId, content);
    }

    public void removeWordFromJoinMessage(String messageId, String content) {
        removeWordFromMessage(joinMessages, messageId, content);
    }

    public void deleteDeathMessage(String messageId) {
        deleteMessage(deathMessages, messageId);
    }

    public void deleteDisconnectMessage(String messageId) {
        deleteMessage(disconnectMessages, messageId);
    }

    public void deleteJoinMessage(String messageId) {
        deleteMessage(joinMessages, messageId);
    }

    public void removeWordFromMessage(List<Map<String, Object>> messages, String messageId, String wordContent) {
        for (Map<String, Object> message : messages) {
            if (messageId.equals(message.get("id"))) {
                List<Map<String, Object>> words = (List<Map<String, Object>>) message.get("words");
                for (int i = 0; i < words.size(); i++) {
                    Map<String, Object> word = words.get(i);
                    String content = (String) word.get("content");
                    if (wordContent.equals(content)) {
                        words.remove(i);
                        save();
                        return;
                    }
                }
                throw new RuntimeException("Word not found: " + wordContent);
            }
        }
    }

    public List<String> getWordsByTypeAndId(String messageType, String messageId) {
        List<Map<String, Object>> messages;
        switch (messageType.toLowerCase()) {
            case "death":
                messages = deathMessages;
                break;
            case "disconnect":
                messages = disconnectMessages;
                break;
            case "join":
                messages = joinMessages;
                break;
            default:
                return Collections.emptyList();
        }

        Optional<Map<String, Object>> optionalMessage = messages.stream()
                .filter(message -> messageId.equals(message.get("id")))
                .findFirst();

        return optionalMessage.map(message -> {
            List<Map<String, Object>> words = (List<Map<String, Object>>) message.get("words");
            return words.stream()
                    .map(word -> (String) word.get("content"))
                    .collect(Collectors.toList());
        }).orElse(Collections.emptyList());
    }

    private void deleteMessage(List<Map<String, Object>> messages, String messageId) {
        messages.removeIf(message -> messageId.equals(message.get("id")));
        save();
    }

    public List<String> getDeathMessageIds() {
        return getMessageIds(deathMessages);
    }

    public List<String> getDisconnectMessageIds() {
        return getMessageIds(disconnectMessages);
    }

    public List<String> getJoinMessageIds() {
        return getMessageIds(joinMessages);
    }

    private List<String> getMessageIds(List<Map<String, Object>> messages) {
        return messages.stream()
                .map(message -> {
                    Object messageId = message.get("id");
                    return messageId != null ? messageId.toString() : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void setDefaultConfigValues() {
        List<Map<String, Object>> deathMessageWords = Arrays.asList(
                createWord("%player%", "red", List.of("bold")),
                createWord("has", null, null),
                createWord("succumbed", null, null),
                createWord("in", null, null),
                createWord("the", null, null),
                createWord("battle...", null, null)
        );

        List<Map<String, Object>> deathMessage = Collections.singletonList(
                createMessage("deathMessage1", deathMessageWords)
        );

        List<Map<String, Object>> disconnectMessageWords = Arrays.asList(
                createWord("%player%", "gray", null),
                createWord("has", null, null),
                createWord("lost", null, null),
                createWord("connection...", null, null)
        );

        List<Map<String, Object>> disconnectMessage = Collections.singletonList(
                createMessage("disconnectMessage1", disconnectMessageWords)
        );

        List<Map<String, Object>> joinMessageWords = Arrays.asList(
                createWord("%player%", "green", null),
                createWord("has", null, null),
                createWord("joined", null, null),
                createWord("the", null, null),
                createWord("game.", null, null)
        );

        List<Map<String, Object>> joinMessage = Collections.singletonList(
                createMessage("joinMessage1", joinMessageWords)
        );

        enabledDeathMessages = true;
        enabledDisconnectMessages = true;
        enabledJoinMessages = true;

        deathMessages = deathMessage;
        disconnectMessages = disconnectMessage;
        joinMessages = joinMessage;

        save();
    }
}

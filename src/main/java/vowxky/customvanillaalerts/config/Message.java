package vowxky.customvanillaalerts.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Message {
    private final String id;
    private List<Word> words = new ArrayList<>();

    public Message(String id, List<Word> words) {
        this.id = id;
        this.words = words;
    }

    public Message(Map<String, Object> messageMap) {
        this.id = (String) messageMap.get("id");
        List<Map<String, Object>> wordsMapList = (List<Map<String, Object>>) messageMap.get("words");
        this.words = wordsMapList.stream()
                .map(Word::new)
                .collect(Collectors.toList());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);

        List<Map<String, Object>> wordsList = words.stream()
                .map(Word::toMap)
                .collect(Collectors.toList());
        map.put("words", wordsList);

        return map;
    }

    public void addWord(Word word) {
        words.add(word);
    }

    public void removeWord(int wordIndex) {
        if (wordIndex >= 0 && wordIndex < words.size()) {
            words.remove(wordIndex);
        } else {
            System.out.println("Invalid word index: " + wordIndex);
        }
    }

    public void editWord(int wordIndex, String newContent, String newColor, List<String> newStyles) {
        if (wordIndex >= 0 && wordIndex < words.size()) {
            Word word = words.get(wordIndex);
            word.edit(newContent, newColor, newStyles);
        } else {
            System.out.println("Invalid word index: " + wordIndex);
        }
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public String getId() {
        return id;
    }

    public List<Word> getWords() {
        return words;
    }
}
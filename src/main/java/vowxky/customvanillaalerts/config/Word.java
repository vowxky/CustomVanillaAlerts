package vowxky.customvanillaalerts.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Word {
    private String color;
    private List<String> style;
    private String content;

    public Word(String color, List<String> style, String content) {
        this.color = color;
        this.style = style;
        this.content = content;
    }

    public Word(Map<String, Object> wordMap) {
        this.color = (String) wordMap.get("color");
        this.style = (List<String>) wordMap.get("style");
        this.content = (String) wordMap.get("content");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("color", color);
        map.put("style", style);
        map.put("content", content);
        return map;
    }

    public String getContent() {
        return content;
    }

    public void edit(String newContent, String newColor, List<String> newStyles) {
        this.content = newContent;
        this.color = newColor;
        this.style = newStyles != null ? newStyles : Collections.emptyList();
    }
}

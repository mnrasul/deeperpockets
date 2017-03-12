package ca.rasul.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Component
public class CategoryMapper {
    @Autowired @Qualifier("categoryMap")
    private Map<String, String> categoryMap;

    public String determineCategory(String memo, String name){
        String input = (memo == null? name: memo);
        if (input == null){
            return null;
        }
        for (String key: categoryMap.keySet()) {
            if (input.toLowerCase().contains(key.toLowerCase())) {
                return categoryMap.get(key);
            }
        }
        return null;
    }

    public Map<String, String> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(final Map<String, String> categoryMap) {
        this.categoryMap = categoryMap;
    }
}

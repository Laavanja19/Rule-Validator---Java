package org.example;

import java.util.List;
import java.util.Map;
public class StringWrappers {
    private String value;

    public StringWrappers(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static Object wrapStrings(Object node) {
        if (node instanceof String) {
            return new StringWrappers((String) node);
        }
        else if (node instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) node;
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                entry.setValue(wrapStrings(entry.getValue()));
            }
            return map;
        }
        else if (node instanceof List) {
            List<Object> list = (List<Object>) node;
            for (int i = 0; i < list.size(); i++) {
                list.set(i, wrapStrings(list.get(i)));
            }
            return list;
        }
        else {
            return node;
        }
    }
}
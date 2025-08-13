package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.JsonPath;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.JsonPath;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.w3c.dom.Node;

import java.text.Format;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;



public class Document {

    private String documentString;
    private Object document = null;

    public Document(String documentString) {
        Object yamlData = new Load(LoadSettings.builder().build()).loadFromString(documentString);
        if (yamlData == null) {
            return;
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        this.documentString = gson.toJson(yamlData);

        this.document = JsonPath.parse(this.documentString).json();
    }

    public Object getRootDocument() {
        return this.document;
    }

    public static Object getMetaData(Object currentNode, NodeMetaData key) {
        Object parentNode = TraversalProperties.getParent(currentNode);
        switch(key) {
            case LENGTH:
                if (parentNode instanceof List) {
                    return ((List<?>) parentNode).size();
                } else if (parentNode instanceof Map) {
                    return ((Map<?, ?>) parentNode).size();
                } else {
                    return null;
                }
            case PROPERTY:
                return getPropertyName(currentNode);
            case PARENT_PROPERTY:
                if (parentNode != null) {
                    return getPropertyName(parentNode);
                } else {
                    return null;
                }
            case PARENT:
                return parentNode;

            default:
                return null;
        }
    }

    public static String getPropertyName(Object node) {
        Object parent = TraversalProperties.getParent(node);
        if (parent == null) {
            return null;
        }
        if (parent instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) parent;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getValue() == node) {
                    return entry.getKey().toString();
                }
            }
        } else if (parent instanceof ArrayList) {
            List<?> list = (List<?>) parent;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == node) {
                    return String.valueOf(i);
                }
            }
        }
        return null;
    }

    public static boolean hasAdvancedFeatures(String givenPath) {
        String[] unsupportedFeatures = {
                "@.length",
                "@property",
                "@path",
                "@parent",
                "@root",
                "~",
                "@parentProperty",
                "@root",
                "^",
                "@number",
                "@match"
        };

        for (String pattern : unsupportedFeatures) {
            if (Pattern.compile(pattern).matcher(givenPath).find()) {
                return true;
            }
        }
        return false;
    }


}
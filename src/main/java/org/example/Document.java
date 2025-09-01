package org.example;

import com.jayway.jsonpath.JsonPath;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.*;


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
        Object rootDocument = StringWrappers.wrapStrings(this.document);
        //System.out.println(PreProcessing.assignPaths(jsonPathExpression,rootDocument));
        //TraversalProperties.getTraversalMap(document);

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
                return getPropertyName(parentNode);
            case PARENT:
                return parentNode;

            default:
                return null;
        }
    }

    public static Object getCurrent(Object node,String field) {
        if (node instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) node;
            if (map.containsKey(field)) {
                return map.get(field);
            }
        }
        return null;
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
                "@",
                "@.length",
                "@property",
                "@path",
                "@parent",
                "@root",
                "~",
                "@parentProperty",
                "^",
                "@number",
                "@match"
        };

        for (String pattern : unsupportedFeatures) {
            if(givenPath.contains(pattern)){
                return true;
            }
        }
        return false;
    }
}

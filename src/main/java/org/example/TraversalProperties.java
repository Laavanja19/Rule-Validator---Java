package org.example;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.example.Document;


public class TraversalProperties {
    private static final IdentityHashMap<Object,Object> traversalMap = new IdentityHashMap<>();
    private static TraversalProperties instance;
    private Object rootDocument;

    private TraversalProperties(Object rootDocument) {
        this.rootDocument = rootDocument;
    }

    public static TraversalProperties getTraversalMap(Object rootDocument) {
        if(instance == null) {
            instance = new TraversalProperties(rootDocument);
        }
        return instance;
    }

    public static void generateParentChildMap(Object current, Object parent) {
        System.out.println("The new value: " + Document.getMetaData(current,NodeMetaData.LENGTH));
        if (parent != null) {
            traversalMap.put(current, parent);
        }
        if (current instanceof Map) {
            for (Map.Entry<?, ?> entry: ((Map<?, ?>) current).entrySet()) {
                if (entry.getKey() instanceof String) {
                    generateParentChildMap(entry.getValue(), current);
                }
            }
        } else if (current instanceof List) {
            List<?> list = (List<?>) current;
            for (int i = 0; i < list.size(); i++) {
                generateParentChildMap(list.get(i), current);
            }
        }
    }

    public static Object getParent(Object currentNode) {
        return traversalMap.get(currentNode);
    }

    public static void printPropertyNames() {
        System.out.println("Property values of the nodes");
        for (Map.Entry<Object, Object> entry : traversalMap.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println("Length: " + Document.getMetaData(entry.getKey(), NodeMetaData.LENGTH));
            System.out.println("Hashcode: " + entry.getKey().hashCode());
            System.out.println("Parent Property: " + Document.getMetaData(entry.getKey(), NodeMetaData.PARENT_PROPERTY));
            System.out.println("Parent: " + Document.getMetaData(entry.getKey(), NodeMetaData.PARENT));
            System.out.println("Property: " + Document.getMetaData(entry.getKey(), NodeMetaData.PROPERTY));
            System.out.println("----------");
        }
    }

    public static void printParentChildRelations() {
        System.out.println("Child → Parent Relations:");
        for (Map.Entry<Object, Object> entry : traversalMap.entrySet()) {
            System.out.println("Child: " + entry.getKey());
            System.out.println("Parent: " + entry.getValue());
            System.out.println("----------");
        }
    }

}
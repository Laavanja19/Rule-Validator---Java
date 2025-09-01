package org.example;

import java.util.*;

import org.example.Document;
public class TraversalProperties {
    private static final IdentityHashMap<Object, Object> traversalMap = new IdentityHashMap<>();
    private static final HashMap<Object,Object> parentMap = new HashMap<>();
    private static TraversalProperties instance;
    private static Object rootDocument;

    private TraversalProperties(Object rootDocument) {
        this.rootDocument = rootDocument;
        generateParentChildMap(rootDocument, null);
    }

    public static TraversalProperties getTraversalMap(Object rootDocument) {
        if (instance == null) {
            instance = new TraversalProperties(rootDocument);
        }
        return instance;
    }

    public static void generateParentChildMap(Object current, Object parent) {
        if (parent != null) {
            traversalMap.put(current, parent);
        }
        if (current instanceof Map) {
            for (Map.Entry<?, ?> entry: ((Map<?, ?>) current).entrySet()) {
                Object value = wrapObjects(entry.getValue());
                //((Map<Object,Object>) current).put(entry.getKey(),value);
                generateParentChildMap(value,current);
            }
        } else if (current instanceof List) {
            List<?> list = (List<?>) current;
            for (int i = 0; i < list.size(); i++) {
                Object item = wrapObjects(list.get(i));
                ((List<Object>) list).set(i,item);
                generateParentChildMap(item, current);
            }
        }
    }

    public static Object wrapPrint(Object value) {
        if (value instanceof String) {
            return new StringWrapper((String) value);
        }
        else if(value instanceof Number) {
            return new NumberWrapper((Number) value);
        }
        else if(value instanceof Boolean) {
            return new BooleanWrapper((Boolean) value);
        }
        return value;
    }


    public static Object getParent(Object currentNode) {
        if (currentNode == null) return null;
        Object wrapped = wrapPrint(currentNode);
        return traversalMap.get(wrapped);
    }

    public static class StringWrapper {
        public String value;
        public StringWrapper(String value) {
            this.value = value;
        }
        @Override
        public String toString(){
            return value;
        }
    }

    public static class NumberWrapper {
        public Number value;
        public NumberWrapper(Number value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    public static class BooleanWrapper {
        public Boolean value;
        public BooleanWrapper(Boolean value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    public static Object wrapObjects(Object value) {
        if(value instanceof String) {
            return new StringWrapper((String) value);
        }
        else if(value instanceof Number) {
            return new NumberWrapper((Number) value);
        }
        else if(value instanceof Boolean) {
            return new BooleanWrapper((Boolean) value);
        }
        return value;
    }
}
















//    public static void printPropertyNames() {
//        System.out.println("Property values of the nodes");
//        for (Map.Entry<Object, Object> entry : traversalMap.entrySet()) {
//            System.out.println(entry.getKey());
//            System.out.println("Length: " + Document.getMetaData(entry.getKey(), NodeMetaData.LENGTH));
//            System.out.println("Hashcode: " + entry.getKey().hashCode());
//            System.out.println("Parent Property: " + Document.getMetaData(entry.getKey(), NodeMetaData.PARENT_PROPERTY));
//            System.out.println("Parent: " + Document.getMetaData(entry.getKey(), NodeMetaData.PARENT));
//            System.out.println("Property: " + Document.getMetaData(entry.getKey(), NodeMetaData.PROPERTY));
//            System.out.println("----------");
//        }
//    }
//
//    public static void printParentChildRelations() {
//        System.out.println("Child → Parent Relations:");
//        for (Map.Entry<Object, Object> entry : traversalMap.entrySet()) {
//            System.out.println("Child: " + entry.getKey());
//            System.out.println("Parent: " + entry.getValue());
//            System.out.println("----------");
//        }
//    }




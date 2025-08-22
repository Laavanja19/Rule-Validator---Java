package org.example;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.example.Document;


public class MetaPredicate {
    public static class PredicateFeatures implements Predicate {
        private final String jsonPathExpression;
        private Object rootDocument;

        public PredicateFeatures(String jsonPathExpression, Object doc) {
            this.jsonPathExpression = jsonPathExpression;
            this.rootDocument = doc;

            TraversalProperties.getTraversalMap(rootDocument);
        }

        @Override
        public boolean apply(PredicateContext context) {
            Object currentNode = context.item();
            String expression = processAdvancedFeatures(jsonPathExpression,currentNode);
            System.out.println(expression);
            int index, endIndex = 0;
            String reducedExpr = "";
            if(expression.contains("?")) {
                index = expression.indexOf("?");
                endIndex = expression.indexOf("]");
                reducedExpr = expression.substring(index+1,endIndex);
            } else {
                index = expression.indexOf("(");
                endIndex = expression.indexOf(")");
                reducedExpr = expression.substring(index+1,endIndex);
            }
            System.out.println(reducedExpr);
              return true;
        }
    }


    public static String processAdvancedFeatures(String jsonPathExpression, Object currentNode) {
        String result = jsonPathExpression;
        if (jsonPathExpression.contains("@.length")) {
            result = replaceLength(result,currentNode);
        }
        if(jsonPathExpression.contains("@property")) {
            result = replaceProperty(result,currentNode);
        }
        if(jsonPathExpression.contains("@parentProperty")) {
            result = replaceParentProperty(result,currentNode);
        }
        if(jsonPathExpression.contains("@parent")) {
            result = replaceParent(result,currentNode);
        }
        return result;

    }

    public static String replaceLength(String jsonPathExpression, Object currentNode) {
        String result = jsonPathExpression;
        Object getLengthObject = Document.getMetaData(currentNode,NodeMetaData.LENGTH);
        System.out.println("Length: " + getLengthObject);
        if (getLengthObject instanceof Integer) {
            result= result.replace("@.length", String.valueOf(getLengthObject));
        }
        return result;
    }

    public static String replaceProperty(String jsonPathExpression, Object currentNode) {
        String result = jsonPathExpression;
        Object getPropertyObject = Document.getMetaData(currentNode,NodeMetaData.PROPERTY);
        System.out.println(getPropertyObject);
        if(getPropertyObject instanceof String) {
            result = result.replace("@property", String.valueOf(getPropertyObject));
        }
        return result;
    }

    public static String replaceParentProperty(String jsonPathExpression, Object currentNode) {
        String result = jsonPathExpression;
        Object getParentObject = Document.getMetaData(currentNode,NodeMetaData.PARENT_PROPERTY);
        if(getParentObject instanceof String) {
            result = result.replace("@parentProperty", String.valueOf(getParentObject));
        }
        return result;
    }

    public static String replaceParent(String jsonPathExpression, Object currentNode) {
        String result = jsonPathExpression;
        Object getParent = Document.getMetaData(currentNode,NodeMetaData.PARENT);
        result = result.replace("@parent" ,String.valueOf(getParent));
        return result;
    }


}

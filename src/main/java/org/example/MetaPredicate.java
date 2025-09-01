package org.example;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;


import java.util.*;
import org.apache.commons.jexl3.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.example.Document;
import org.mvel2.MVEL;


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
           // return true;
            Object currentNode = context.item();
            String expression = processAdvancedFeatures(jsonPathExpression, currentNode);
            System.out.println(expression);
            int index, endIndex = 0;
            String reducedExpr = "";
            String evaluatedExpression = "";
            List<?> targetNodes = List.of();
            int newValue = 0 ;
            Boolean boolResult = false;
            if (expression.contains("?")) {
                index = expression.indexOf("?");
                endIndex = expression.indexOf("]");
                reducedExpr = expression.substring(index + 1, endIndex);
            } else {
                index = expression.indexOf("(");
                endIndex = expression.indexOf(")");
                reducedExpr = expression.substring(index + 1, endIndex);
            }
            System.out.println("Reduced Expression: " + reducedExpr);
            Object value = evaluatedResulting(reducedExpr);
            if (!expression.contains("?")) {
                Object parent = Document.getMetaData(currentNode, NodeMetaData.PARENT);
                if (parent instanceof List) {
                    targetNodes = new ArrayList<>((List<?>) parent);
                }
                newValue = (Integer) value;
                boolResult =  (currentNode.equals(targetNodes.get(newValue)));
            } else {
                boolResult = ((Boolean)value == true);
            }

            return boolResult;
            //return true;
        }


        public static String processAdvancedFeatures(String jsonPathExpression, Object currentNode) {
            String result = jsonPathExpression;
            if(jsonPathExpression.contains("@.") && !jsonPathExpression.contains("@.length")) {
                result = replaceCurrent(result,currentNode);
            }
            if (jsonPathExpression.contains("@.length")) {
                result = replaceLength(result, currentNode);
            }
            if (jsonPathExpression.contains("@property")) {
                result = replaceProperty(result, currentNode);
            }
            if (jsonPathExpression.contains("@parentProperty")) {
                result = replaceParentProperty(result, currentNode);
            }
            if (jsonPathExpression.contains("@parent")) {
                result = replaceParent(result, currentNode);
            }
            return result;

        }

        public static String replaceCurrent(String jsonPathExpression, Object currentNode) {
            String result = jsonPathExpression;
            String operators[] = {"===", "!==", ">", ">=", "<", "<="};
            int start = result.indexOf("@");
            int end = 0;
            for(int i=0;i<operators.length;i++) {
                if(result.contains(operators[i])){
                    end = result.indexOf(operators[i]);
                    break;
                }
            }
            String subString = result.substring(start+2,end);
            Object getCurrentObject = Document.getCurrent(currentNode,subString);
            result = result.replace("@."+subString,String.valueOf(getCurrentObject));
            System.out.println("Current: " + getCurrentObject);
            return result;

        }

        public static String replaceLength(String jsonPathExpression, Object currentNode) {
            String result = jsonPathExpression;
            Object getLengthObject = Document.getMetaData(currentNode, NodeMetaData.LENGTH);
            System.out.println("Length: " + getLengthObject);
            if (getLengthObject instanceof Integer) {
                result = result.replace("@.length", String.valueOf(getLengthObject));
            }
            return result;
        }

        public static String replaceProperty(String jsonPathExpression, Object currentNode) {
            String result = jsonPathExpression;
            Object getPropertyObject = Document.getMetaData(currentNode, NodeMetaData.PROPERTY);
            System.out.println(getPropertyObject);
            try {
                Integer.parseInt(String.valueOf(getPropertyObject));
            }
            catch(Exception e) {
                getPropertyObject = "\""+String.valueOf(getPropertyObject)+"\"";
            }
//            if (getPropertyObject instanceof String) {
//                result = result.replace("@property", "\""+String.valueOf(getPropertyObject)+"\"");
//            } else {
//                result = result.replace("@property", String.valueOf(getPropertyObject));
//            }
            result = result.replace("@property", String.valueOf(getPropertyObject));
            return result;
        }

        public static String replaceParentProperty(String jsonPathExpression, Object currentNode) {
            String result = jsonPathExpression;
            Object getParentObject = Document.getMetaData(currentNode, NodeMetaData.PARENT_PROPERTY);
            if (getParentObject instanceof String) {
                result = result.replace("@parentProperty", "\"" + String.valueOf(getParentObject) + "\"");
            }
            else {
                result = result.replace("@parentProperty",String.valueOf(getParentObject));
            }
            return result;
        }

        public static String replaceParent(String jsonPathExpression, Object currentNode) {
            String result = jsonPathExpression;
            Object getParent = Document.getMetaData(currentNode, NodeMetaData.PARENT);
            result = result.replace("@parent", String.valueOf(getParent));
            return result;
        }

        //Using Maven Evaluator
//        public static Object evaluatedResult(String reducedExpression) {
//            Object result = reducedExpression;
//            Object resulting = MVEL.eval((String) result);
//            System.out.println("Resulting: " + resulting);
//            return resulting;
//        }

        //Using Jexl Evaluator
        public static Object evaluatedResulting(String reducedExpression) {
            JexlEngine jexl  = new JexlBuilder().create();
            JexlContext context = new MapContext();
            String expressionString = reducedExpression;
            JexlExpression expression = jexl.createExpression(expressionString);
            Object resulting = expression.evaluate(context);
            System.out.println("Resulting : " + resulting);
            return resulting;
        }

    }
}






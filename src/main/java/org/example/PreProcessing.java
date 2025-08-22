package org.example;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreProcessing {
    public static int countSquareBraces(String jsonPathExpression) {
        String str = jsonPathExpression;
        int count = 0;
        for(int i= 0;i<str.length();i++) {
           if(str.charAt(i) == '[') {
               count++;
           }
       }
        return count;
    }

    public static int countAdvancedSquareBraces(String jsonPathExpression) {
        int countAdvanced = 0;
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(jsonPathExpression);
        while(matcher.find()) {
            String subString = matcher.group(1);
            if(Document.hasAdvancedFeatures(subString)) {
                countAdvanced ++;
            }
        }
        return countAdvanced;
    }

    public static List<String> assignPaths(String jsonPathExpression, Object root) throws Exception {
        Configuration config = Configuration.builder().options().build();
        List<String> paths;
        String replaceExpression= "";
        String reducedExpression = "";
        int index,endIndex = 0;
        if(countAdvancedSquareBraces(jsonPathExpression) == 0) {
            paths = JsonPath.using(config).parse(root).read(jsonPathExpression);
        }
        else if(countAdvancedSquareBraces(jsonPathExpression) == 1) {

            if(Document.hasAdvancedFeatures(jsonPathExpression)) {
                if(jsonPathExpression.contains("?")) {
                    index = jsonPathExpression.indexOf("?");
                    endIndex = jsonPathExpression.indexOf("]");
                } else {
                    index = jsonPathExpression.indexOf("[");
                    endIndex = jsonPathExpression.indexOf("]");
                }
                reducedExpression = jsonPathExpression.substring(index+1,endIndex);
                replaceExpression = jsonPathExpression.replace(reducedExpression,"?");
                System.out.println("Reduced Expression: " + reducedExpression);
                System.out.println("Replace Expression: " + replaceExpression);
            }
            Predicate predicate = new MetaPredicate.PredicateFeatures(jsonPathExpression,root);
            paths = JsonPath.using(config).parse(root).read(replaceExpression,predicate);
        }
        else {
            throw new Exception("Multiple Advanced Features are Not Supported");
        }
        return paths;
    }
}
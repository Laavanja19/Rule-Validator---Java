package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreProcessing {
    public static int countSquareBraces(String jsonPathExpression) {
        String str = jsonPathExpression;
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '[') {
                count++;
            }
        }
        return count;
    }

    public static int countAdvancedSquareBraces(String jsonPathExpression) {
        int countAdvanced = 0;
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(jsonPathExpression);
        while (matcher.find()) {
            String subString = matcher.group(1);
            if (Document.hasAdvancedFeatures(subString)) {
                countAdvanced++;
            }
        }
        return countAdvanced;
    }

    public static List<String> getAdvancedSubstrings(String jsonPathExpression) {
        List<String> advancedStrings = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(jsonPathExpression);
        while(matcher.find()) {
            String subString = matcher.group(1);
            if (Document.hasAdvancedFeatures(subString)) {
                advancedStrings.add(subString);
            }
        }
        return advancedStrings;
    }


    public static List<String> assignPaths(String jsonPathExpression, Object root) throws Exception {
        Configuration config = Configuration.builder().options().build();
        List<String> paths;
        List<String> advancedParts = getAdvancedSubstrings(jsonPathExpression);
        String replaceExpression = "";
        String reducedExpression = "";
        if (countAdvancedSquareBraces(jsonPathExpression) == 0) {
            paths = JsonPath.using(config).parse(root).read(jsonPathExpression);
        } else if (countAdvancedSquareBraces(jsonPathExpression) == 1) {
            advancedParts = getAdvancedSubstrings(jsonPathExpression);
            replaceExpression = jsonPathExpression;

            for (String part : advancedParts) {
                System.out.println("Parts: " + part);
                replaceExpression = replaceExpression.replace(part, "?");
            }

            System.out.println("Advanced Parts: " + advancedParts);
            System.out.println("Final Replace Expression: " + replaceExpression);
            System.out.println(jsonPathExpression);
            Predicate predicate = new MetaPredicate.PredicateFeatures(jsonPathExpression, root);
            paths = JsonPath.using(config).parse(root).read(replaceExpression, predicate);
        } else {
            throw new Exception("Multiple Advanced Features are Not Supported");
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(paths));
        return paths;
    }
}
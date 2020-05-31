package com.translate;

import java.util.HashMap;

public class SubclassTranslator {

    public static String translate(String name) {
        if (SubclassTranslator.mapping.get(name) != null) {
            return SubclassTranslator.mapping.get(name);
        }
        return transform(name);
    }

    private static String transform(String name){
        /* Customize transforming of subclasses */
        return name;
    }

    private final static HashMap<String, String> mapping = new HashMap<>();

    static {
        mapping.put("MapnikSubclassName", "GeoserverSubclassName");
    }
}

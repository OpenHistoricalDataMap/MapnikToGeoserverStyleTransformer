package com.translate;

import java.util.HashMap;

public class ClassTranslator {

    public static String translate(String name) {
        if (ClassTranslator.mapping.get(name) != null) {
            return ClassTranslator.mapping.get(name);
        }
        return name;
    }

    private static final HashMap<String, String> mapping = new HashMap<>();

    static {
        mapping.put("MapnikClassName", "GeoserverClassName");
    }
}

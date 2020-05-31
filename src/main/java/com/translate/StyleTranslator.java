package com.translate;

import java.util.HashMap;

public class StyleTranslator {

    public static String translate(String name) {
        if (StyleTranslator.mapping.get(name) != null) {
            return StyleTranslator.mapping.get(name);
        }
        try {
            return transform(name);
        }catch (Exception e){
            e.printStackTrace();
        }
         return  name;
    }


    private static String transform(String name){
        /* Customize transforming of subclasses */

        /* Remove first ":" of style class */
        return name.substring(1);
    }

    private final static HashMap<String, String> mapping = new HashMap<>();

    static {
        mapping.put("MapnikStyleName", "GeoserverStyleName");
    }
}

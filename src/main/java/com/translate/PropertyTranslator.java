package com.translate;

import com.node.PropertyNode;

import java.util.HashMap;

public class PropertyTranslator {

    public static void translate(PropertyNode n) {
        String name = n.getName();
        if (PropertyTranslator.mapping.get(name) != null) {
            n.setName(PropertyTranslator.mapping.get(name));
        }
        transform(n);
    }

   private static void transform(PropertyNode n){
        /* Customize transforming of property name or value */
    }

    private final static HashMap<String, String> mapping = new HashMap<>();

    static {
        mapping.put("MapnikPropertyName", "GeoserverPropertyName");
    }
}

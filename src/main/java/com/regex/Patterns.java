package com.regex;

public class Patterns {

    public static final String SELECTOR_START = "[^\\{]*?\\{$";
    public static final String SELECTOR_END = "[^\\{]*?\\}$";
    public static final String PROPERTY = "(.*?):(.*?);";
    public static final String VARIABLE_ASSIGNMENT = "\\@(.*?):(.*?);";

}

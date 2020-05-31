package com.node;

public class VariableNode extends Node {

    private String name;
    private String value;

    public VariableNode(String raw, NodeGroup parent, String name, String value) {
        super(raw, parent);
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return this.name + ": " + this.value + "\n";
    }
}

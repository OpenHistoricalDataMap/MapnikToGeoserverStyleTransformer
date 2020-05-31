package com.node;

public class PropertyNode extends Node {

    private String name;
    private String value;
    private boolean resolved;
    private String styleClass;

    public PropertyNode(String raw, NodeGroup parent, String name, String value) {
        super(raw, parent);
        this.name = name;
        this.value = value;
        this.resolved = !this.value.contains("@");
        String[] nameParts = name.split("/");
        if (nameParts.length == 2) {
            this.styleClass = "::" + nameParts[0];
            this.name = nameParts[1];
        } else {
            this.styleClass = null;
        }
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
        this.resolved = !this.value.contains("@");
    }

    public boolean isResolved() {
        return this.resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    @Override
    public String toString() {
        return this.name + ": " + this.value + " (" + this.resolved + ")\n";
    }

    public String getStyleClass() {
        return this.styleClass;
    }

    public void setStyleClass(String s) {
        this.styleClass = s;
    }
}

package com.node;

public class Node implements Cloneable {
    private String raw;
    private NodeGroup parent;
    private boolean transformed;

    public Node(String raw, NodeGroup parent) {
        this.raw = raw;
        this.parent = parent;
        this.transformed = false;
    }

    String getRaw() {
        return this.raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public NodeGroup getParent() {
        return this.parent;
    }

    void setParent(NodeGroup parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return this.raw + "\n";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean isTransformed() {
        return this.transformed;
    }

    public void setTransformed(boolean transformed) {
        this.transformed = transformed;
    }
}

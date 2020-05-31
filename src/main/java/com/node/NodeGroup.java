package com.node;

import java.util.ArrayList;

public class NodeGroup extends Node {

    private ArrayList<Node> children;

    public NodeGroup(String raw, NodeGroup parent) {
        super(raw, parent);
        this.children = new ArrayList<>();
    }

    public ArrayList<Node> getChildren() {
        return this.children;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
        children.forEach(c -> c.setParent(this));
    }

    public void addChild(Node n) {
        this.children.add(n);
        n.setParent(this);
    }

    public void addChildren(ArrayList<Node> children) {
        children.forEach(this::addChild);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(this.getRaw() + " {\n");
        for (int i = 0; i < this.getChildren().size(); i++) {
            s.append(this.getChildren().get(i).toString());
        }
        s.append("}\n");
        return s.toString();
    }
}

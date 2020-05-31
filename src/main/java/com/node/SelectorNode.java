package com.node;


public class SelectorNode extends NodeGroup {

    private String selector;

    protected SelectorNode(String raw, NodeGroup parent, String selector) {
        super(raw, parent);
        this.selector = selector;
    }

    public String getSelector() {
        return this.selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(this.selector + " {\n");
        for (int i = 0; i < this.getChildren().size(); i++) {
            s.append(this.getChildren().get(i).toString());
        }
        s.append("}\n");
        return s.toString();
    }
}

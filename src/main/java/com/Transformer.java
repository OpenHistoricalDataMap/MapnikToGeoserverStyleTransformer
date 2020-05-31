package com;

import com.node.Node;
import com.node.NodeGroup;
import com.node.PropertyNode;
import com.node.SelectorNode;
import com.node.selector.ClazzSelector;
import com.node.selector.StyleSelector;
import com.node.selector.SubclazzSelector;
import com.node.selector.ZoomSelector;
import com.translate.*;

import java.util.Optional;
import java.util.Stack;

public class Transformer {

    private NodeGroup transformedTree;

    private Stack<String> clazzStack;
    private Stack<String> subclazzStack;
    private Stack<String> zoomStack;
    private Stack<String> styleStack;

    private NodeGroup parent;
    private String clazz;
    private String subclazz;
    private String zoom;
    private String style;


    private String transformClassSelector(ClazzSelector selector) {
        if (selector.isTransformed())
            return selector.getSelector();
        else
            selector.setTransformed(true);
        return ClassTranslator.translate(selector.getSelector());
    }

    private String transformSubclassSelector(SubclazzSelector selector) {
        if (selector.isTransformed())
            return selector.getSelector();
        else
            selector.setTransformed(true);
        return SubclassTranslator.translate(selector.getSelector());
    }

    private String transformZoomSelector(ZoomSelector selector) {
        if (selector.isTransformed())
            return selector.getSelector();
        else
            selector.setTransformed(true);
        return ZoomTranslator.translate(selector.getSelector());
    }

    private String transformStyleSelector(StyleSelector selector) {
        if (selector.isTransformed())
            return selector.getSelector();
        else
            selector.setTransformed(true);
        return StyleTranslator.translate(selector.getSelector());
    }

    private void transformProperty(PropertyNode n) {
        if (n.isTransformed())
            return;
        n.setTransformed(true);
        if (n.getStyleClass() != null) {
            n.setStyleClass(StyleTranslator.translate(n.getStyleClass()));
        }
        PropertyTranslator.translate(n);
    }

    public NodeGroup transformTree(NodeGroup root) {
        this.transformedTree = new NodeGroup("ROOT_TRANSFORMED", null);

        this.clazzStack = new Stack<>();
        this.subclazzStack = new Stack<>();
        this.zoomStack = new Stack<>();
        this.styleStack = new Stack<>();

        this.clazzStack.push(" [undefined]");
        this.subclazzStack.push("[undefined]");
        this.zoomStack.push("[default]");
        this.styleStack.push("*");

        this.clazz = "[undefined]";
        this.subclazz = "[undefined]";
        this.zoom = "[default]";
        this.style = "*";

        root.getChildren().forEach(this::addNodeToTransformedTree);

        return this.transformedTree;
    }

    private void addNodeToTransformedTree(Node n) {
        if (n instanceof SelectorNode) {
            if (n instanceof ClazzSelector) {
                this.clazz = transformClassSelector((ClazzSelector) n);
                this.clazzStack.push(this.clazz);
                createClazzSelector();
                ((ClazzSelector) n).getChildren().forEach(this::addNodeToTransformedTree);
                this.clazzStack.pop();
                this.clazz = this.clazzStack.peek();

            } else if (n instanceof SubclazzSelector) {

                this.subclazz = transformSubclassSelector((SubclazzSelector) n);
                this.subclazzStack.push(this.subclazz);
                createSubclazzSelector();
                ((SubclazzSelector) n).getChildren().forEach(this::addNodeToTransformedTree);
                this.subclazzStack.pop();
                this.subclazz = this.subclazzStack.peek();

            } else if (n instanceof ZoomSelector) {
                this.zoom = transformZoomSelector((ZoomSelector) n);
                this.zoomStack.push(this.zoom);
                createZoomSelector();
                ((ZoomSelector) n).getChildren().forEach(this::addNodeToTransformedTree);
                this.zoomStack.pop();
                this.zoom = this.zoomStack.peek();
            } else if (n instanceof StyleSelector) {
                this.style = transformStyleSelector((StyleSelector) n);
                this.styleStack.push(this.style);
                createStyleSelector();
                ((StyleSelector) n).getChildren().forEach(this::addNodeToTransformedTree);
                this.styleStack.pop();
                this.style = this.styleStack.peek();
            }
        } else if (n instanceof PropertyNode) {
            boolean hasStyleClass = ((PropertyNode) n).getStyleClass() != null;
            transformProperty((PropertyNode) n);
            if (hasStyleClass) {
                this.style = ((PropertyNode) n).getStyleClass();
                this.styleStack.push(this.style);
            }
            createStyleSelector().addChild(n);
            if (hasStyleClass) {
                this.styleStack.pop();
                this.style = this.styleStack.peek();
            }
        }
    }

    private ClazzSelector createClazzSelector() {
        Optional<Node> parent = this.transformedTree.getChildren().stream().filter(c -> c instanceof ClazzSelector && ((ClazzSelector) c).getSelector().equals(this.clazz)).findFirst();
        if (parent.isPresent()) {
            return (ClazzSelector) parent.get();
        } else {
            ClazzSelector clazzSelector = new ClazzSelector("", null, this.clazz);
            this.transformedTree.addChild(clazzSelector);
            return clazzSelector;
        }
    }

    private SubclazzSelector createSubclazzSelector() {
        ClazzSelector clazzSelector = createClazzSelector();
        Optional<Node> parent = clazzSelector.getChildren().stream().filter(c -> c instanceof SubclazzSelector && ((SubclazzSelector) c).getSelector().equals(this.subclazz)).findFirst();
        if (parent.isPresent()) {
            return (SubclazzSelector) parent.get();
        } else {
            SubclazzSelector subclazzSelector = new SubclazzSelector("", null, this.subclazz);
            clazzSelector.addChild(subclazzSelector);
            return subclazzSelector;
        }
    }

    private ZoomSelector createZoomSelector() {
        SubclazzSelector subclazzSelector = createSubclazzSelector();
        Optional<Node> parent = subclazzSelector.getChildren().stream().filter(c -> c instanceof ZoomSelector && ((ZoomSelector) c).getSelector().equals(this.zoom)).findFirst();
        if (parent.isPresent()) {
            return (ZoomSelector) parent.get();
        } else {
            ZoomSelector zoomSelector = new ZoomSelector("", null, this.zoom);
            subclazzSelector.addChild(zoomSelector);
            return zoomSelector;
        }
    }

    private SelectorNode createStyleSelector() {
        ZoomSelector zoomSelector = createZoomSelector();
        Optional<Node> parent = zoomSelector.getChildren().stream().filter(c -> c instanceof StyleSelector && ((StyleSelector) c).getSelector().equals(this.style)).findFirst();
        if (parent.isPresent()) {
            return (StyleSelector) parent.get();
        } else {
            StyleSelector styleSelector = new StyleSelector("", null, this.style);
            zoomSelector.addChild(styleSelector);
            return styleSelector;
        }
    }

}

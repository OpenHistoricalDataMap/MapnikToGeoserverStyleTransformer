package com;

import com.logging.Logger;
import com.node.*;
import com.node.selector.ClazzSelector;
import com.node.selector.StyleSelector;
import com.node.selector.SubclazzSelector;
import com.node.selector.ZoomSelector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.regex.Patterns.*;

public class MapnikReader {

    private static final Pattern singleSelectorPattern = Pattern.compile("(#[^\\s\\[]*|\\[[^]]*\\])");
    private static final Pattern urlPattern = Pattern.compile("url\\('(.*?)'\\)");
    private static final Pattern variablePattern = Pattern.compile("(@\\S*)");

    /**
     * will transform nested selector into single selector
     * Note: all selector classes are correct after
     *
     * example: [subclass][zoom] { ... } will be transformed to [subclass] { [zoom] { ...} }
     *
     * @param group node group
     */
    private static void splitNestedSelectors(NodeGroup group) {
        ArrayList<Node> children = group.getChildren();
        ArrayList<Node> newChildren = new ArrayList<>();
        for (Node child : children) {
            if (!(child instanceof SelectorNode)) {
                newChildren.add(child);
                continue;
            }

            SelectorNode nestedSelectorNode = (SelectorNode) child;
            String selector = nestedSelectorNode.getSelector();
            ArrayList<String> matches = new ArrayList<>();
            Matcher m = singleSelectorPattern.matcher(selector);
            while (m.find()) {
                matches.add(m.group(1));
            }

            if (matches.size() <= 1) {
                newChildren.add(child);
                continue;
            }

            Logger.debug("Split nested selector of " + selector + " into " + matches.size() + " sub selectors");
            ArrayList<Node> childrenOfOriginalSelector = nestedSelectorNode.getChildren();
            NodeGroup tmpParent = nestedSelectorNode;

            for (int i = 0; i < matches.size(); i++) {
                if (i == 0) {
                    SelectorNode sn = parseSelectorNode(matches.get(i));
                    newChildren.add(sn);
                    tmpParent = sn;
                    continue;
                }

                SelectorNode sn = parseSelectorNode(matches.get(i));
                Logger.debug("selector " + matches.get(i) + " was changed from " + nestedSelectorNode.getClass().getSimpleName() + " to " + sn.getClass().getSimpleName());
                tmpParent.addChild(sn);
                tmpParent = sn;

                if (i == matches.size() - 1) {
                    sn.setChildren(childrenOfOriginalSelector);
                    ArrayList<Node> cs = new ArrayList<>();
                    cs.add(sn);
                    nestedSelectorNode.setChildren(cs);
                }
            }
        }

        group.setChildren(newChildren);

        for (Node n : group.getChildren()) {
            if (n instanceof NodeGroup)
                splitNestedSelectors((NodeGroup) n);
        }
    }

    /**
     * will transform selector groups to single selector
     * Noe: child nodes will be cloned
     *
     * example: [subclass][zoom], [subclass] { ... }
     * will be transformed to
     * [subclass][zoom] {...}
     * [subclass] { ... }
     *
     * @param group node group
     * @throws CloneNotSupportedException
     */
    private static void splitSelectorGroup(NodeGroup group) throws CloneNotSupportedException {

        ArrayList<Node> children = group.getChildren();
        ArrayList<Node> nodesToAdd = new ArrayList<>();

        for (Node child : children) {
            if (!(child instanceof SelectorNode))
                continue;

            SelectorNode selectorGroupNode = (SelectorNode) child;
            String selector = selectorGroupNode.getSelector();
            String[] selectorGroups = selector.split(",");

            if (selectorGroups.length <= 1)
                continue;

            Logger.debug("Split selector of " + selector + " into " + selectorGroups.length + " selectors");
            for (int i = 0; i < selectorGroups.length; i++) {

                if (i == 0) {
                    selectorGroupNode.setSelector(selectorGroups[i].trim());
                    continue;
                }

                SelectorNode clone = (SelectorNode) child.clone();
                clone.setSelector(selectorGroups[i].trim());
                nodesToAdd.add(clone);
            }
        }

        if (nodesToAdd.size() > 0) {
            group.addChildren(nodesToAdd);
            /* call with same group again to split new created selector groups */
            splitSelectorGroup(group);
            return;
        }

        for (Node n : group.getChildren()) {
            if (n instanceof NodeGroup)
                splitSelectorGroup((NodeGroup) n);
        }
    }

    /**
     * Note: selector nodes doesn't have to / won't be correct at this time because
     * of nested and grouped selectors. That will be fixed in splitSelectorGroup and splitNestedSelectors
     *
     * example: [subclass][zoom] { ... } will be (wrongly) identified as ZoomSelector
     *
     * @param blocks raw string
     * @return parsed node
     */
    private static NodeGroup parseNodes(String[] blocks) {
        NodeGroup root = new NodeGroup("ROOT", null);
        NodeGroup currentParent = root;

        for (String block : blocks) {
            block = block.trim();
            Node node = parseNode(block);

            if (node == null) {
                currentParent = currentParent.getParent();
            } else {
                Logger.debug(block + " was identified as " + node.getClass().getSimpleName());
                currentParent.addChild(node);
                if (isSelectorStart(block)) {
                    currentParent = (NodeGroup) node;
                }
            }
        }
        try {
            splitSelectorGroup(root);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        splitNestedSelectors(root);
        return root;
    }

    private static Node parseNode(String raw) {
        if (isSelectorStart(raw)) {
            return parseSelectorNode(raw);
        } else if (isSelectorEnd(raw)) {
            return null;
        } else if (isVariableAssignment(raw)) {
            VariableNode vn = parseVariableNode(raw);
            /* cache variable */
            VariableCache.getInstance().set(vn.getName(), vn.getValue());
            return vn;
        } else if (isProperty(raw)) {
            return parsePropertyNode(raw);
        } else {
            return new Node(raw, null);
        }
    }

    private static PropertyNode parsePropertyNode(String raw) {
        String[] components = raw.split(":");
        String name = components[0].trim();
        String value = components[1].trim();
        value = value.substring(0, value.length() - 1);
        PropertyNode pn = new PropertyNode(raw, null, name, value);

        Matcher m = urlPattern.matcher(value);
        while (m.find()) {
            ResourceManager.getInstance().put(m.group(1));
        }

        if (!pn.isResolved()) {
            String currentValue = pn.getValue();
            Matcher ma = variablePattern.matcher(value);
            while (ma.find()) {
                String variableName = ma.group(1);
                String variableValue = VariableCache.getInstance().get(variableName);
                if (variableValue != null) {
                    Logger.debug("took cached variable value " + variableValue + " for " + variableName);
                    currentValue = currentValue.replaceAll(variableName, variableValue);
                }
            }
            pn.setValue(currentValue);
            pn.setResolved(true);
        }
        return pn;
    }

    private static VariableNode parseVariableNode(String raw) {
        String[] components = raw.split(":");
        String name = components[0].trim();
        String value = components[1].trim();
        value = value.substring(0, value.length() - 1);
        return new VariableNode(raw, null, name, value);
    }

    private static SelectorNode parseSelectorNode(String raw) {
        final String selectorValue = raw.replaceAll("\\{", "").trim();
        if (raw.contains("#")) {
            return new ClazzSelector(raw, null, selectorValue);
        } else if (raw.contains("zoom")) {
            return new ZoomSelector(raw, null, selectorValue);
        } else if (raw.contains("::")) {
            return new StyleSelector(raw, null, selectorValue);
        }
        return new SubclazzSelector(raw, null, selectorValue);
    }

    public static NodeGroup[] parseFiles(File[] files) {
        Logger.debug("===== Start processing of " + files.length + " files =====");
        Logger.debug("===== Start analyzing vars =====");
        boolean debug = Logger.debug;
        Logger.debug = false;
        Arrays.stream(files).forEach(f -> {
            try {
                Logger.debug("Start analyzing vars of " + f.getName());
                parseFile(f);
                Logger.success("Finished analyzing vars of " + f.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Logger.success("Finished analyzing vars");
        Logger.success("Gathered " + VariableCache.getInstance().getCache().entrySet().size() + " variables");
        /* Parse files twice to make sure all vars across all files a cached */
        ArrayList<NodeGroup> parsedFiles = new ArrayList<>();
        Logger.debug = debug;
        Logger.debug("===== Start parsing nodes =====");
        Arrays.stream(files).forEach(f -> {
            try {
                Logger.debug("Start parsing nodes of" + f.getName());
                parsedFiles.add(parseFile(f));
                Logger.success("Finished analyzing nodes of " + f.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Logger.success("Finished analyzing nodes");
        Logger.success("Gathered " + ResourceManager.getInstance().getCache().entrySet().size() + " resources");
        return parsedFiles.toArray(new NodeGroup[0]);
    }

    private static NodeGroup parseFile(File f) throws IOException {
        NodeGroup root;
        String content;
        String[] blocks;

        content = prepareContent(new String(Files.readAllBytes(f.toPath())));
        blocks = getBlocks(content);
        root = parseNodes(blocks);

        return root;
    }

    private static String[] getBlocks(String content) {
        return content.split("(?<=;|\\{|\\})");
    }

    private static String prepareContent(String content) {
        String regex;
        Pattern pattern;
        Matcher matcher;

        /* remove comments */
        content = removeComments(content);

        /* remove linebreaks */
        regex = "\\r?\\n|\\r";
        pattern = Pattern.compile(regex, Pattern.MULTILINE);
        matcher = pattern.matcher(content);
        content = matcher.replaceAll("");

        return content;
    }

    private static String removeComments(String content) {
        String regex;
        Pattern pattern;
        Matcher matcher;

        regex = "\\/\\*[\\s\\S]*?\\*\\/";
        pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        matcher = pattern.matcher(content);
        content = matcher.replaceAll("");

        regex = "\\/\\/.*(?=(\\n|\\r))";
        pattern = Pattern.compile(regex, Pattern.MULTILINE);
        matcher = pattern.matcher(content);
        content = matcher.replaceAll("");
        return content;
    }

    private static boolean isProperty(String raw) {
        return raw.matches(PROPERTY);
    }

    private static boolean isSelectorStart(String raw) {
        return raw.matches(SELECTOR_START);
    }

    private static boolean isSelectorEnd(String raw) {
        return raw.matches(SELECTOR_END);
    }

    private static boolean isVariableAssignment(String raw) {
        return raw.matches(VARIABLE_ASSIGNMENT);
    }
}

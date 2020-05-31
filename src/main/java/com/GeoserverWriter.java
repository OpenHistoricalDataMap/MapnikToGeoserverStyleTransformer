package com;

import com.node.Node;
import com.node.NodeGroup;
import com.node.PropertyNode;
import com.node.SelectorNode;
import com.node.selector.ClazzSelector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GeoserverWriter {

    private BufferedWriter out;
    private final File outputDirectory;

    public GeoserverWriter(File outputDirectory){
        this.outputDirectory = outputDirectory;
    }

    public void writeFile(NodeGroup root, File file) {

        try {
            String filename = file.getName().replaceFirst("[.][^.]+$", "");
            File textFile = new File(this.outputDirectory, filename + ".css");
            this.out = new BufferedWriter(new FileWriter(textFile));
            root.getChildren().forEach(c -> writeNode(c, 0));
            this.out.flush();
            this.out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeNode(Node n, int depth) {
        String indent = generateIndent(depth);
        if (n instanceof SelectorNode) {
            try {
                boolean isClazzSelector = n instanceof ClazzSelector;
                String selectorString = indent;
                if(isClazzSelector)
                    selectorString += "[";
                selectorString += ((SelectorNode) n).getSelector();
                if(isClazzSelector)
                    selectorString += "]";
                selectorString += " {\n";
                this.out.write(selectorString);
                ((SelectorNode) n).getChildren().forEach(c -> writeNode(c, depth + 1));
                this.out.write(indent + "}\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (n instanceof PropertyNode) {
            try {
                this.out.write(indent + ((PropertyNode) n).getName() + ": " + ((PropertyNode) n).getValue() + ";\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String generateIndent(int depth){
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < depth; i++){
            s.append("\t");
        }
        return s.toString();
    }
}

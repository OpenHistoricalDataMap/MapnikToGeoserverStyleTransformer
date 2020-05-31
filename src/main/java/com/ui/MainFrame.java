package com.ui;


import com.GeoserverWriter;
import com.MapnikReader;
import com.ResourceManager;
import com.Transformer;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.logging.Logger;
import com.node.NodeGroup;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton chooseFilesButton;
    private JButton chooseDirectoryButton;
    private JCheckBox debugCheckBox;
    private File[] filesToTransform = new File[0];
    private File outputDirectory = new File(System.getProperty("user.home"));


    private MainFrame() {

        setContentPane(this.contentPane);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        getRootPane().setDefaultButton(this.buttonOK);

        this.buttonOK.addActionListener(e -> onOK());

        this.buttonCancel.addActionListener(e -> onCancel());

        this.chooseFilesButton.addActionListener(e -> onChooseFiles());

        this.chooseDirectoryButton.addActionListener(e -> onChooseDirectory());

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        this.contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        this.buttonOK.setText("Transforming...");
        new Thread(() -> {

            if (MainFrame.this.filesToTransform.length > 0) {
                Logger.debug = this.debugCheckBox.isSelected();
                File inputDirectory = MainFrame.this.filesToTransform[0].getParentFile();
                GeoserverWriter geoserverWriter = new GeoserverWriter(MainFrame.this.outputDirectory);
                NodeGroup[] untransformedTrees = MapnikReader.parseFiles(MainFrame.this.filesToTransform);
                for (int i = 0; i < MainFrame.this.filesToTransform.length; i++) {
                    Logger.debug("Start transforming of " + this.filesToTransform[i].getName());
                    NodeGroup transformedRoot = new Transformer().transformTree(untransformedTrees[i]);
                    Logger.success("Finished transforming of " + this.filesToTransform[i].getName());
                    Logger.debug("Start writing of " + this.filesToTransform[i].getName());
                    geoserverWriter.writeFile(transformedRoot, MainFrame.this.filesToTransform[i]);
                    Logger.success("Finished writing of " + this.filesToTransform[i].getName());
                }
                Logger.debug("Start copying resource files");
                ResourceManager.getInstance().copyResources(inputDirectory, MainFrame.this.outputDirectory);
                Logger.success("Finished copying resource files");
                try {
                    Desktop.getDesktop().open(MainFrame.this.outputDirectory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            MainFrame.this.buttonOK.setText("Transform");
        }).start();
    }

    private void onCancel() {
        dispose();
    }

    private void onChooseFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(true);
        chooser.setDialogTitle("Choose files to transform");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Mapnik Style Sheets (.mss)", "mss");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.filesToTransform = chooser.getSelectedFiles();
            this.buttonOK.setEnabled(this.filesToTransform.length > 0);
            this.buttonOK.setText(this.filesToTransform.length > 0 ? "Transform (" + this.filesToTransform.length + ")" : "Transform");
        }
        this.repaint();
        this.revalidate();
    }

    private void onChooseDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(true);
        chooser.setDialogTitle("Choose output directory");
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.outputDirectory = chooser.getSelectedFile();
        }
        this.repaint();
        this.revalidate();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        MainFrame dialog = new MainFrame();
        dialog.setTitle("MapnikToGeoserverTransformer");
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel1, gbc);
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setEnabled(false);
        buttonOK.setText("Transform");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel3, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Choose files to transform (.mss)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel3.add(label1, gbc);
        chooseFilesButton = new JButton();
        chooseFilesButton.setText("Choose files");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel3.add(chooseFilesButton, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Choose output directory");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label2, gbc);
        chooseDirectoryButton = new JButton();
        chooseDirectoryButton.setText("Choose directory");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(chooseDirectoryButton, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel3.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel3.add(spacer5, gbc);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer6, gbc);
        debugCheckBox = new JCheckBox();
        debugCheckBox.setSelected(true);
        debugCheckBox.setText("debug");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(debugCheckBox, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}

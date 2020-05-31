package com.ui;

import com.GeoserverWriter;
import com.MapnikReader;
import com.ResourceManager;
import com.Transformer;
import com.logging.Logger;
import com.node.NodeGroup;

import javax.swing.*;
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
}

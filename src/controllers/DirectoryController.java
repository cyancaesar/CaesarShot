package controllers;

import views.Home;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class DirectoryController implements ActionListener {
    public Home HomeGui;
    public DirectoryController(Home Home)
    {
        this.HomeGui = Home;
    }
    public void DirectoryChooserDialog()
    {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(1);
        int ResultSelection = jFileChooser.showDialog(null, "Select Directory");
        if (ResultSelection == JFileChooser.APPROVE_OPTION)
        {
            File DirectoryPath = jFileChooser.getSelectedFile();
            HomeGui.setOutputDirectory(DirectoryPath.toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DirectoryChooserDialog();
    }
}

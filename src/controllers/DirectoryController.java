package controllers;

import views.Home;
import views.MainClass;

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
        JFrame frame = new JFrame();
        frame.setIconImages(MainClass.ICONS);
        jFileChooser.setFileSelectionMode(1);
        int ResultSelection = jFileChooser.showDialog(frame, "Select Directory");
        if (ResultSelection == JFileChooser.APPROVE_OPTION)
        {
            File DirectoryPath = jFileChooser.getSelectedFile();
            HomeGui.setOutputDir(DirectoryPath.toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DirectoryChooserDialog();
    }
}

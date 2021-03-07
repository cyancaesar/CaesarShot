package controllers;

import model.Image;
import model.Shot;
import views.Home;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FullscreenController implements ActionListener {
    public Home HomeGui;
    public FullscreenController(Home Home)
    {
        this.HomeGui = Home;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String Filename;
        String Filetype;
        Filename = JOptionPane.showInputDialog(null, "Enter Filename:", "Filename", JOptionPane.PLAIN_MESSAGE);
        Object[] filetypes = {"png", "jpg", "gif", "jpeg"};
        Filetype = (String)JOptionPane.showInputDialog(null, "Enter Filetype:", "Filetype", JOptionPane.PLAIN_MESSAGE, null ,filetypes, filetypes[0]);
        if (!HomeGui.getOutputDirectoryPath().equals("..."))
        {
            Image.writeImage(HomeGui, Shot.fullscreenShot(), Filename, Filetype);
        }
        else
        {
            System.out.println("Error, not a valid directory");
        }
    }
}

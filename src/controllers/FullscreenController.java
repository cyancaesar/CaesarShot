package controllers;

import models.FileGuard;
import models.Shot;
import views.Home;
import views.MainClass;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class FullscreenController implements ActionListener {
    private FileGuard fileGuard;
    private Shot shot;
    public Home HomeGui;
    public FullscreenController(Home Home)
    {
        this.HomeGui = Home;
        this.shot = new Shot();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String outputDirectory = HomeGui.getOutputDirectory();
        if (!outputDirectory.equals("..."))
        {
            this.fileGuard = new FileGuard(this, HomeGui.getOutputDirectory());
        }
        else
        {
            this.fileGuard = new FileGuard(this);
        }
        try {
            HomeGui.hideFrame();
            fileGuard.ImageWriting(shot.fullscreenShot());
            MainClass.sleep();
            HomeGui.showFrame();
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
    }
}

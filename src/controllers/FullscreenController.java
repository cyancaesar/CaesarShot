package controllers;

import models.FileGuard;
import models.Shot;
import views.Home;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FullscreenController extends MessageDispatcher implements ActionListener {
    private FileGuard fileGuard;
    private Shot shot;
    private BufferedImage image;
    public Home homeGui;
    public FullscreenController(Home Home)
    {
        this.homeGui = Home;
        this.shot = new Shot();
    }
    private void takeShot()
    {
        String outputDirectory = homeGui.getOutputDirectory();
        if (!outputDirectory.equals("..."))
        {
            this.fileGuard = new FileGuard(this, outputDirectory);
        }
        else
        {
            this.fileGuard = new FileGuard(this);
        }
        shot.fullscreenShot();
        image = shot.getImage();
        try
        {
            boolean indicator = fileGuard.writeOut(image);
            if (indicator)
            {
                messageWriter("Image has been successfully saved under\n" + fileGuard.GetImagePath(), 1);
            }
            else
            {
                messageWriter("Failed to save the image", 0);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        takeShot();
    }
}

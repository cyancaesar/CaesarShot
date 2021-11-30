package controllers;

import models.FileIO;
import models.Shot;
import views.Home;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FullscreenController extends MessageDispatcher implements ActionListener {
    private FileIO fileIO;
    private Shot shot;
    private BufferedImage image;
    public Home home;

    public FullscreenController(Home Home)
    {
        this.home = Home;
        this.shot = new Shot();
    }

    private void initialize()
    {
        fileIO = FileIO.getInstance();
        shot.fullShot();
        image = shot.getImage();
        if (Home.OUTPUT_DIR_SET)
        {
            String path = home.getOutputDir();
            fileIO.setPath(path);
        }
        else
        {
            fileIO.setDefaultPath();
        }
        try
        {
            boolean indicator = fileIO.write(image);
            if (indicator)
            {
                messageWriter("Image has been successfully saved under\n" + fileIO.getImagePath(), 1);
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
        initialize();
    }
}

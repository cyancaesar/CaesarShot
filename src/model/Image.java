package model;

import views.Home;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.*;
public class Image {
    public static void writeImage(Home HomeGui, BufferedImage Image, String Filename, String Filetype)
    {
        StringBuilder Builder = new StringBuilder();
        Builder.append(HomeGui.getOutputDirectoryPath())
                .append("//")
                .append(Filename)
                .append('.')
                .append(Filetype);
        try
        {
            ImageIO.write(Image, Filetype, new File( Builder.toString() ));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

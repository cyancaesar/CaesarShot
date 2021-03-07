package model;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Shot {

    public static BufferedImage fullscreenShot()
    {
        BufferedImage Image = null;
        try
        {
            Robot robot = new Robot();
            Image = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return Image;
    }
}

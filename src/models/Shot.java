package models;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Shot {
    Robot robot;
    BufferedImage image;

    public BufferedImage fullscreenShot()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        image = null;
        try
        {
            robot = new Robot();
            image = robot.createScreenCapture(new Rectangle(screenSize));
            return image;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return image;
    }
}

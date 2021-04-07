package models;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Shot {
    public Robot robot;
    public BufferedImage image;

    public void fullscreenShot()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        image = null;
        try
        {
            robot = new Robot();
            image = robot.createScreenCapture(new Rectangle(screenSize));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void snippetShot(int baseX, int baseY, int finalX, int finalY)
    {
        Rectangle rec;
        if (baseX < finalX && baseY < finalY)
        {
            rec = new Rectangle(baseX, baseY, Math.abs(baseX-finalX), Math.abs(baseY-finalY));
        }
        else
        {
            rec = new Rectangle(finalX, finalY, Math.abs(baseX-finalX), Math.abs(baseY-finalY));
        }
        System.out.println(rec.getX());
        image = null;
        try
        {
            robot = new Robot();
            image = robot.createScreenCapture(rec);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public BufferedImage getImage() {
        return image;
    }
}

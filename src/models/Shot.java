package models;

import views.MainClass;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;

public class Shot {
    public Robot robot;
    public BufferedImage image;
    public BufferedImage masterImage;
    private String copyrightFontPath = "D:\\Browser Downloads\\Fonts\\rainyhearts\\rainyhearts.ttf";

    public void fullscreenShot()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        image = null;
        try
        {
            robot = new Robot();
            image = robot.createScreenCapture(new Rectangle(screenSize));
            MainClass.playSound("thatsamazing.wav");
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

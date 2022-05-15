package models;

import views.MainClass;

import java.awt.*;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.util.List;

public class Shot {
    public Robot robot;
    public BufferedImage image;

    public BufferedImage snippetHelper()
    {
        BufferedImage imageTemp = null;
        try
        {
            imageTemp = new Robot().createScreenCapture(new Rectangle(MainClass.SCREEN_DIMENSION));
            image = imageTemp;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return imageTemp;
    }
    public void fullShot()
    {
        image = null;
        try
        {
            robot = new Robot();
            image = robot.createScreenCapture(new Rectangle(MainClass.SCREEN_DIMENSION));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void snippetShot(int xBase, int yBase, int xFinal, int yFinal)
    {
        Rectangle rec;
        int width = Math.abs(xBase-xFinal);
        int height = Math.abs(yBase-yFinal);
        if (xBase < xFinal && yBase < yFinal)
        {
            rec = new Rectangle(xBase, yBase, width, height);
        }
        else
        {
            rec = new Rectangle(xFinal, yFinal, width, height);
        }
        image = null;
        try
        {
            robot = new Robot();
            BaseMultiResolutionImage multiResolutionScreenCapture = (BaseMultiResolutionImage) robot.createMultiResolutionScreenCapture(rec);
            List<Image> imageList = multiResolutionScreenCapture.getResolutionVariants();
            image = imageList.size() > 1 ? (BufferedImage) imageList.get(1) : (BufferedImage) imageList.get(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public BufferedImage getImage() {
        return image;
    }
    public void setImage(BufferedImage image)
    {
        this.image = image;
    }
}

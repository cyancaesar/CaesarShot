package models;

import views.MainClass;
import java.awt.*;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
        image = null;
        try
        {
            robot = new Robot();
            BaseMultiResolutionImage imageMulti = (BaseMultiResolutionImage) robot.createMultiResolutionScreenCapture(rec);
            List<Image> imageList = imageMulti.getResolutionVariants();
            if (imageList.size() > 1)
            {
                image = (BufferedImage) imageList.get(1);
            }
            else
            {
                image = (BufferedImage) imageList.get(0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public BufferedImage snippetCopy()
    {
        BufferedImage masterImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = masterImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(image, 0, 0, masterImage.getWidth(), masterImage.getHeight(), null);
        g2d.setColor(Color.YELLOW);
        Font font = null;
        try
        {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(MainClass.CURRENT_DIR.getFile() + FileGuard.COPYRIGHT_FONT_PATH));
        }
        catch (FontFormatException | IOException fontFormatException)
        {
            fontFormatException.printStackTrace();
        }
        assert font != null;
        g2d.setFont(font.deriveFont(16f));
        g2d.drawString("Captured with CaesarShot", 5, image.getHeight()-5);
        g2d.dispose();
        return masterImage;
    }

    public BufferedImage getImage() {
        return image;
    }
}

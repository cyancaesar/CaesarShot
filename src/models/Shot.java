package models;

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
            masterImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = masterImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            g2d.setColor(Color.YELLOW);
            g2d.setBackground(Color.CYAN);
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(copyrightFontPath));
            g2d.setFont(font.deriveFont(16f));
            g2d.drawString("Captured with CaesarShot", 5, image.getHeight()-5);
            g2d.dispose();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public BufferedImage getImage() {
        return masterImage;
    }
}

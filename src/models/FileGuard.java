package models;

import controllers.FullscreenController;
import controllers.SnippetController;
import views.MainClass;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileGuard {
    private FullscreenController fullscreenController;
    private SnippetController snippetController;
    private File file;
    private static final String DOCUMENT_PATH = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
    private static final String DIRECTORY_NAME = "\\CaesarShot";
    public final static String COPYRIGHT_FONT_PATH = "\\assets\\RainyHearts.ttf";
    private String filename = "shot_";
    private String path;

    private boolean DirectoryChecker()
    {
        this.file = new File(this.path);
        if (!file.exists())
        {
            // Create Directory
            return file.mkdir();
        }
        else
        {
            return true;
        }
    }

    private void SetFilename()
    {
        String[] files = file.list();
        assert files != null;
        int digit = files.length + 1;
        this.filename += digit;
    }

    public String GetImagePath()
    {
        return path + GetFilename() + ".png";
    }

    public String GetFilename()
    {
        return this.filename;
    }

    public boolean writeOut(BufferedImage image) throws IOException
    {
        File output;
        output = new File(path + GetFilename() + ".png");

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
            font = Font.createFont(Font.TRUETYPE_FONT, new File(MainClass.CURRENT_DIR.getFile() + COPYRIGHT_FONT_PATH));
        }
        catch (FontFormatException fontFormatException)
        {
            fontFormatException.printStackTrace();
        }
        assert font != null;
        g2d.setFont(font.deriveFont(16f));
        g2d.drawString("Captured with CaesarShot", 5, image.getHeight()-5);
        g2d.dispose();
        return ImageIO.write(masterImage, "png", output);
    }

    public FileGuard(FullscreenController fc)
    {
        this(fc, DOCUMENT_PATH + DIRECTORY_NAME);
    }
    public FileGuard(FullscreenController fc, String path)
    {
        this.path = path + "\\";
        this.fullscreenController = fc;
        if (DirectoryChecker())
        {
            SetFilename();
        }
    }
    public FileGuard(SnippetController sc)
    {
        this(sc, DOCUMENT_PATH + DIRECTORY_NAME);
    }
    public FileGuard(SnippetController sc, String path)
    {
        this.path = path + "\\";
        this.snippetController = sc;
        if (DirectoryChecker())
        {
            SetFilename();
        }
    }
}

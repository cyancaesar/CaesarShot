package models;

import controllers.FullscreenController;
import controllers.SnippetController;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.swing.JFileChooser;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileGuard {
    private FullscreenController fullscreenController;
    private SnippetController snippetController;
    private File file;
    private static final String DOCUMENT_PATH = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
    private static final String DIRECTORY_NAME = "\\CaesarShot";
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
        return ImageIO.write(image, "png", output);
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

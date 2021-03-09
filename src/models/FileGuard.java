package models;

import controllers.FullscreenController;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileGuard {
    private FullscreenController fullscreenController;
    private File file;
    public static String DOCUMENT_PATH = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
    public static String DIRECTORY_NAME = "\\CaesarShot\\";
    private String filename = "shot_";
    private String path = null;

    public boolean DirectoryCreator()
    {
        return file.mkdir();
    }

    public boolean DirectoryChecker()
    {
        this.file = new File(DOCUMENT_PATH + DIRECTORY_NAME);
        if (!file.exists())
        {
            return this.DirectoryCreator();
        }
        return true;
    }

    private int FetchLastNumber()
    {
        String[] files = file.list();
        assert files != null;
        return files.length + 1;
    }

    public void SetFilename()
    {
        this.filename += FetchLastNumber();
    }

    public String GetFilename()
    {
        return this.filename;
    }

    public boolean ImageWriting(BufferedImage image) throws IOException {
        File output;
        if (path != null)
        {
            output = new File(path + GetFilename() + ".png");
        }
        else
        {
            output = new File(DOCUMENT_PATH + DIRECTORY_NAME + GetFilename() + ".png");
        }
        return ImageIO.write(image, "png", output);
    }

    public FileGuard(FullscreenController fc)
    {
        this.fullscreenController = fc;
        if (DirectoryChecker())
        {
            SetFilename();
        }
    }
    public FileGuard(FullscreenController fc, String path)
    {
        this.path = path;
        this.fullscreenController = fc;
        if (DirectoryChecker())
        {
            SetFilename();
        }
    }

}

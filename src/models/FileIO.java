package models;

import controllers.FullscreenController;
import controllers.SnippetController;
import controllers.WideKeyListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class FileIO {
    private static FileIO FILE_IO = null;
    private static final String CONFIG_PATH = System.getProperty("user.home");
    private FullscreenController fullscreenController;
    private SnippetController snippetController;
    private File file;
    private static final String DOCUMENT_PATH = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
    private static final String DIRECTORY_NAME = "\\CaesarShot";
    private final String prefix = "shot_";
    private String filename;
    private String path;

    private boolean dirCheck(String path)
    {
        file = new File(path);
        if (!file.exists())
        {
            return file.mkdir();
        }
        return true;
    }

    private void filenameCounter()
    {
        String[] files = file.list((file1, s) -> s.startsWith(prefix));
        assert files != null;
        int digit = files.length + 1;
        this.filename = prefix.concat(String.valueOf(digit));
    }

    public void setPath(String path)
    {
        if (dirCheck(path))
        {
            filenameCounter();
            this.path = path.concat("\\").concat(filename);
        }

    }

    public void setDefaultPath()
    {
        setPath(DOCUMENT_PATH + DIRECTORY_NAME);
    }

    public String getDefaultPath()
    {
        return DOCUMENT_PATH + DIRECTORY_NAME;
    }

    public String getImagePath()
    {
        return path.concat(".png");
    }

    public boolean write(BufferedImage image) throws IOException
    {
        File outputFile;
        String path = getImagePath();
        outputFile = new File(path);
        BufferedImage masterImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = masterImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(image, 0, 0, masterImage.getWidth(), masterImage.getHeight(), null);
        g2d.dispose();
        return ImageIO.write(masterImage, "png", outputFile);
    }

    public void readConfig()
    {
        if (new File(CONFIG_PATH + "/.caesarshot/config.ser").exists())
        {
            try
            {
                FileInputStream fis = new FileInputStream(CONFIG_PATH + "/.caesarshot/config.ser");
                ObjectInputStream ois = new ObjectInputStream(fis);
                Config c = (Config) ois.readObject();
                Config.getInstance().setShortcutCode(c.getShortcutCode());
                ois.close();
                fis.close();
            }
            catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void writeConfig(Config config)
    {
        try
        {
//            FileOutputStream fos = new FileOutputStream("E:\\IdeaProjects\\CaesarShot\\src\\configs\\config.ser");
            if (!new File(CONFIG_PATH + "/.caesarshot").exists())
            {
                boolean state = new File(CONFIG_PATH + "/.caesarshot").mkdir();
                if (!state)
                    JOptionPane.showMessageDialog(null, "Config file cannot be saved.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            FileOutputStream fos = new FileOutputStream(CONFIG_PATH + "/.caesarshot/config.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(config);
            oos.close();
            fos.flush();
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    synchronized public static FileIO getInstance()
    {
        if (FILE_IO == null)
        {
            FILE_IO = new FileIO();
        }
        return FILE_IO;
    }

}

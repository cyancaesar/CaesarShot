package controllers;

import models.Action;
import models.Drawer;
import models.FileIO;
import models.Shot;
import models.Sticker;
import views.Home;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SnippetController extends MessageDispatcher implements ActionListener, WindowListener {
    private Drawer drawerPanel;
    private FileIO fileIO;
    private final Shot shot;
    private BufferedImage image;
    public Home homeGui;

    public SnippetController(Home homeGui)
    {
        this.homeGui = homeGui;
        this.shot = new Shot();
    }

    public void init()
    {
        BufferedImage fullscreenImage = shot.snippetHelper();
        this.drawerPanel = new Drawer(this, fullscreenImage);
    }

    public void shotSnippet(int xBase, int yBase, int xFinal, int yFinal)
    {
        shot.snippetShot(xBase, yBase, xFinal, yFinal);
    }

    public void saveShot(BufferedImage image)
    {
        String outputDirectory = homeGui.getOutputDir();
        if (!outputDirectory.equals("..."))
        {
            this.fileIO = FileIO.getInstance();
            this.fileIO.setPath(outputDirectory);

        }
        else
        {
            this.fileIO = FileIO.getInstance();
            this.fileIO.setDefaultPath();
        }
        try
        {
            boolean indicator = fileIO.write(image);
            if (indicator)
            {
                messageWriter("Image has been successfully saved under\n" + fileIO.getImagePath(), 1);
            }
            else
            {
                messageWriter("Failed to save the image", 0);
            }
            if (!WideKeyListener.FROM_WIDE_KEY_LISTENER)
                this.homeGui.frame.setVisible(true);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void fireShot()
    {
        String outputDirectory = homeGui.getOutputDir();
        if (outputDirectory.equals("..."))
        {
            fileIO = FileIO.getInstance();
            fileIO.setDefaultPath();
        }
        else
        {
            fileIO = FileIO.getInstance();
            fileIO.setPath(outputDirectory);
        }
        BufferedImage image = shot.getImage();
        try
        {
            boolean indicator = fileIO.write(image);
            if (indicator)
            {
                messageWriter("Image has been successfully saved under\n" + fileIO.getImagePath(), 1);
            }
            else
            {
                messageWriter("Failed to save the image", 0);
            }
            if (!WideKeyListener.FROM_WIDE_KEY_LISTENER)
                this.homeGui.frame.setVisible(true);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public boolean isDrawerAlive()
    {
        return drawerPanel != null && (drawerPanel.isShowing());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WideKeyListener.FROM_WIDE_KEY_LISTENER = false;
        homeGui.frame.setVisible(false);
        init();
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (Drawer.action == Action.SAVE_IMAGE)
        {
            fireShot();
        }
        else if (Drawer.action == Action.HOVER_SNIPPET)
        {
            image = shot.getImage();
            new Sticker(this);
//            IntelliJTheme.install(MainClass.loader.getResourceAsStream("assets/space-gray.theme.json"));
//            SwingUtilities.updateComponentTreeUI(MainClass.MAIN_FRAME.frame);
        }
        else if (Drawer.action == Action.COPY_IMAGE)
        {
            image = shot.getImage();
            ClipboardController.setClipboard(image);
        }
        else if(Drawer.action == Action.EXIT)
        {
            if (!WideKeyListener.FROM_WIDE_KEY_LISTENER)
                this.homeGui.frame.setVisible(true);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}

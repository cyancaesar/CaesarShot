package controllers;

import models.Drawer;
import models.FileGuard;
import models.Shot;
import models.Sticker;
import views.Home;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SnippetController extends MessageDispatcher implements Controllable, ActionListener, WindowListener {
    private Drawer drawerPanel;
    private FileGuard fileGuard;
    private final Shot shot;
    private BufferedImage image;
    private int[] coordinates;
    public Home homeGui;

    public SnippetController(Home homeGui)
    {
        this.homeGui = homeGui;
        this.shot = new Shot();
    }

    public void init()
    {
//        setImage(shot.getImage());
        this.drawerPanel = new Drawer(this);
    }

    public void setCoordinates(int baseX, int baseY, int width, int height)
    {
        this.coordinates = new int[] {baseX, baseY, width, height};
    }

    public void saveShot(BufferedImage image)
    {
        String outputDirectory = homeGui.getOutputDirectory();
        if (!outputDirectory.equals("..."))
        {
            this.fileGuard = new FileGuard(this, outputDirectory);
        }
        else
        {
            this.fileGuard = new FileGuard(this);
        }
        try
        {
            boolean indicator = fileGuard.writeOut(image);
            if (indicator)
            {
                messageWriter("Image has been successfully saved under\n" + fileGuard.GetImagePath(), 1);
            }
            else
            {
                messageWriter("Failed to save the image", 0);
            }
            if (!WideKeyListener.FROM_WIDE_KEY_LISTENER)
                this.homeGui.Frame.setVisible(true);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void fireShot()
    {
        String outputDirectory = homeGui.getOutputDirectory();
        if (!outputDirectory.equals("..."))
        {
            this.fileGuard = new FileGuard(this, outputDirectory);
        }
        else
        {
            this.fileGuard = new FileGuard(this);
        }
        shot.snippetShot(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
        setImage(shot.getImage());
        try
        {
            boolean indicator = fileGuard.writeOut(image);
            if (indicator)
            {
                messageWriter("Image has been successfully saved under\n" + fileGuard.GetImagePath(), 1);
            }
            else
            {
                messageWriter("Failed to save the image", 0);
            }
            if (!WideKeyListener.FROM_WIDE_KEY_LISTENER)
                this.homeGui.Frame.setVisible(true);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public boolean isDrawerAlive()
    {
        if (this.drawerPanel != null)
        {
            return (this.drawerPanel.isShowing());
        }
        else
        {
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WideKeyListener.FROM_WIDE_KEY_LISTENER = false;
        homeGui.Frame.setVisible(false);
        init();
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

    @Override
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (Drawer.SAVE_IMAGE)
        {
            fireShot();
        }
        else if (Drawer.HOVER_SNIPPET)
        {
            shot.snippetShot(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
            image = shot.getImage();
            Sticker stickerPanel = new Sticker(this);
        }
        else if (Drawer.COPY_IMAGE)
        {
            shot.snippetShot(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
            image = (Home.COPYRIGHT) ? shot.snippetCopy() : shot.getImage();
            ClipboardController.setClipboard(image);
        }
        else if(Drawer.EXIT_MARKER)
        {
            if (!WideKeyListener.FROM_WIDE_KEY_LISTENER)
                this.homeGui.Frame.setVisible(true);
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

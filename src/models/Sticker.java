package models;

import controllers.ClipboardController;
import controllers.SnippetController;
import controllers.WideKeyListener;
import views.MainClass;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Sticker extends JComponent implements MouseListener, MouseMotionListener, KeyListener, WindowListener {
    protected static int SCREEN_WIDTH = MainClass.SCREEN_DIMENSION.width;
    protected static int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    private static final int RIGHT_SIDE_REGION = SCREEN_WIDTH/7;
    private static int IMAGE_ID = 0;
    private static boolean RESIZE_PRESSED = false;

    public SnippetController snippetController;
    private CircleWindow saveWindow;
    private CircleWindow copyWindow;
    private CircleWindow deleteWindow;
    private int xMouse;
    private int yMouse;

    private final BufferedImage image;
    private Image scaledImage;
    private int imageWidth;
    private int imageHeight;

    private final JFrame frame;
    private boolean mouseEntered;
    private final Border unpressedBorder = BorderFactory.createLineBorder(Color.WHITE, 0, true);
    private final Border pressedBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3, true);
    private int xInit;
    private int yInit;

    public Sticker(SnippetController sc)
    {
        setDoubleBuffered(true);

        AWTEventListener listener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                KeyEvent evt = (KeyEvent) event;
                if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_S) {
                    Drawer.action = Action.EXIT;
                    frame.dispose();
                    snippetController.saveShot(image);
                }
            }
        };

        this.snippetController = sc;
        image = snippetController.getImage();
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();
        this.scaledImage = image.getScaledInstance(imageWidth, -1, Image.SCALE_AREA_AVERAGING);

        this.frame = new JFrame();
        this.setBorder(unpressedBorder);

        frame.setTitle(String.valueOf(++IMAGE_ID));
        frame.setIconImages(MainClass.ICONS);
        frame.getContentPane().add(this);
        frame.setUndecorated(true);
        frame.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
        frame.addKeyListener(this);
        frame.addWindowListener(this);
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, KeyEvent.KEY_EVENT_MASK);

        frame.setSize(imageWidth, imageHeight);
        frame.setLocationRelativeTo(null);

        saveWindow = new CircleWindow(this, MainClass.SAVE);
        copyWindow = new CircleWindow(this, MainClass.COPY);
        deleteWindow = new CircleWindow(this, MainClass.DELETE);

        saveWindow.addMouseListener(saveWindow);
        copyWindow.addMouseListener(copyWindow);
        deleteWindow.addMouseListener(deleteWindow);

    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(this.scaledImage, 0, 0, null);
    }

    private void rescale(int x)
    {
        try
        {
            this.scaledImage = image.getScaledInstance(x, -1, Image.SCALE_AREA_AVERAGING);
            this.imageWidth = scaledImage.getWidth(this);
            this.imageHeight = scaledImage.getHeight(this);
        }
        catch (Exception e)
        {
            System.out.println("Error on resizing the image");
        }
        repaint();
        frame.setSize(this.imageWidth, this.imageHeight);
    }

    public void exit()
    {
        CircleWindow.CLEAR_WINDOWS = true;
        saveWindow.startOut();
        copyWindow.startOut();
        deleteWindow.startOut();
        IMAGE_ID--;
        if (!WideKeyListener.FROM_WIDE_KEY_LISTENER)
        {
            this.frame.dispose();
            this.snippetController.homeGui.frame.setVisible(true);
        }
        else
        {
            this.frame.dispose();
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if (frame.getCursor().getType() == Cursor.NW_RESIZE_CURSOR || frame.getCursor().getType() == Cursor.NE_RESIZE_CURSOR)
        {
            Sticker.RESIZE_PRESSED = true;
            return;
        }
        else
        {
            Sticker.RESIZE_PRESSED = false;
        }
        xInit = e.getX();
        yInit = e.getY();
        this.setBorder(pressedBorder);
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (!CircleWindow.isShowing)
        {
            copyWindow.window.setAlwaysOnTop(false);
            saveWindow.window.setAlwaysOnTop(false);
            deleteWindow.window.setAlwaysOnTop(false);
            copyWindow.window.setAlwaysOnTop(true);
            saveWindow.window.setAlwaysOnTop(true);
            deleteWindow.window.setAlwaysOnTop(true);
        }

        if (Sticker.RESIZE_PRESSED)
        {
            setBorder(pressedBorder);
            try
            {
                rescale(e.getX());
            }
            catch (Exception exc)
            {
                System.out.println("Error");
            }
            return;
        }

        xMouse = e.getXOnScreen();
        yMouse = e.getYOnScreen();
        frame.setLocation(xMouse - xInit, yMouse - yInit);
        this.setBorder(pressedBorder);
        if (xMouse >= SCREEN_WIDTH-RIGHT_SIDE_REGION)
        {
            if (!CircleWindow.isShowing)
            {
                saveWindow.startIn();
                copyWindow.startIn();
                deleteWindow.startIn();
            }
        }
        else
        {
            if (CircleWindow.isShowing)
            {
                saveWindow.startOut();
                copyWindow.startOut();
                deleteWindow.startOut();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (copyWindow.inRange)
        {
            ClipboardController.setClipboard(image);
            exit();
        }
        else if (saveWindow.inRange)
        {
            Drawer.action = Action.EXIT;
            exit();
            snippetController.saveShot(image);
            frame.dispose();
        }
        else if (deleteWindow.inRange)
        {
            exit();
        }
        else
        {
            if (CircleWindow.isShowing)
            {
                saveWindow.animatorIn.stop();
                copyWindow.animatorIn.stop();
                deleteWindow.animatorIn.stop();
                saveWindow.startOut();
                copyWindow.startOut();
                deleteWindow.startOut();
            }
        }
        this.setBorder(unpressedBorder);
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        if (mouseEntered)
            setBorder(pressedBorder);
        else
            setBorder(unpressedBorder);

        if (e.getX() > imageWidth - 30 && e.getX() <= imageWidth && e.getY() > imageHeight - 30 && e.getY() <= imageHeight)
        {
            frame.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
        }
        else if (e.getX() < imageWidth - (imageWidth - 30) && e.getX() >= 0 && e.getY() > imageHeight - 30 && e.getY() <= imageHeight)
        {
            frame.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
        }
        else
        {
            frame.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        }
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
//        if (e.getKeyChar() == 'q')
//        {
//            exit();
//        }
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        mouseEntered = true;
        setBorder(pressedBorder);
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        mouseEntered = false;
        setBorder(unpressedBorder);

    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

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
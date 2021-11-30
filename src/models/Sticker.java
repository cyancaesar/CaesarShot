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
    protected static int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    protected static int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
    private static int RIGHT_SIDE_REGION = SCREEN_WIDTH/7;
    private static int IMAGE_ID = 0;
    private static boolean RESIZE_PRESSED = false;

    public SnippetController snippetController;
    public Circle circleCopy;
    public Circle circleDelete;
    private int xMouse;
    private int yMouse;
    private double xMinCopy;
    private double xMinDelete;
    private double xMaxCopy;
    private double xMaxDelete;
    private double yMinCopy;
    private double yMinDelete;
    private double yMaxCopy;
    private double yMaxDelete;

    private BufferedImage image;
    private Image scaledImage;
    private int imageWidth;
    private int imageHeight;

    private final JFrame frame;
    private final Border unpressedBorder = BorderFactory.createLineBorder(Color.WHITE, 0, true);
    private final Border pressedBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3, true);
    private int xInit;
    private int yInit;

    public Sticker(SnippetController sc)
    {
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


        circleCopy = new Circle(this, "copy");
        circleDelete = new Circle(this, "delete");
        circleCopy.setImage(MainClass.COPY);
        circleDelete.setImage(MainClass.DELETE);

        xMinCopy = circleCopy.getAbsoluteLocation().getX();
        xMaxCopy = circleCopy.getAbsoluteLocation().getX()+circleCopy.frame.getWidth();
        xMinDelete = circleDelete.getAbsoluteLocation().getX();
        xMaxDelete = circleDelete.getAbsoluteLocation().getX()+circleDelete.frame.getWidth();

        yMinCopy = circleCopy.getAbsoluteLocation().getY();
        yMaxCopy = circleCopy.getAbsoluteLocation().getY()+circleCopy.frame.getHeight();
        yMinDelete = circleDelete.getAbsoluteLocation().getY();
        yMaxDelete = circleDelete.getAbsoluteLocation().getY()+circleDelete.frame.getHeight();

        frame.setSize(imageWidth, imageHeight);
        frame.setLocationRelativeTo(null);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(this.scaledImage, 0, 0, null);
//        Graphics2D g2d = (Graphics2D)g;
//        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        g2d.drawImage(image, null, 0, 0);
    }

    private void rescale(int x)
    {
        this.scaledImage = image.getScaledInstance(x, -1, Image.SCALE_AREA_AVERAGING);
        this.imageWidth = scaledImage.getWidth(this);
        this.imageHeight = scaledImage.getHeight(this);
        repaint();
        frame.setSize(this.imageWidth, this.imageHeight);
    }

    public void clearUp()
    {
        IMAGE_ID--;
        Circle.CIRCLE_OBJECTS -= 2;
        circleCopy.exit();
        circleDelete.exit();
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
    public void mousePressed(MouseEvent e) {
        if (frame.getCursor().getType() == Cursor.E_RESIZE_CURSOR)
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
    public void mouseReleased(MouseEvent e) {
        if (xMouse >= xMinCopy && xMouse <= xMaxCopy && yMouse >= yMinCopy && yMouse <= yMaxCopy)
        {
            clearUp();
            ClipboardController.setClipboard(image);
        }
        else if (xMouse >= xMinDelete && xMouse <= xMaxDelete && yMouse >= yMinDelete && yMouse <= yMaxDelete)
            clearUp();
        else
            if (circleCopy.isAlive() && circleDelete.isAlive())
            {
                circleCopy.hideCircle();
                circleDelete.hideCircle();
            }
            this.setBorder(unpressedBorder);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (Sticker.RESIZE_PRESSED)
        {
            rescale(e.getX());
            return;
        }
        xMouse = e.getXOnScreen();
        yMouse = e.getYOnScreen();
        frame.setLocation(xMouse - xInit, yMouse - yInit);
        this.setBorder(pressedBorder);

        if (xMouse >= SCREEN_WIDTH-RIGHT_SIDE_REGION)
        {
            circleCopy.getIn = xMouse >= xMinCopy && xMouse <= xMaxCopy && yMouse >= yMinCopy && yMouse <= yMaxCopy;
            circleDelete.getIn = xMouse >= xMinDelete && xMouse <= xMaxDelete && yMouse >= yMinDelete && yMouse <= yMaxDelete;

            circleCopy.requestRepaint();
            circleDelete.requestRepaint();
            if (!circleCopy.isAlive())
                circleCopy.showCircle();
            if (!circleDelete.isAlive())
                circleDelete.showCircle();
        }
        else
        {
            if (circleCopy.isAlive())
                circleCopy.hideCircle();
            if (circleDelete.isAlive())
                circleDelete.hideCircle();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (e.getX() > imageWidth-10 && e.getX() < imageWidth)
        {
            frame.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
        }
        else
        {
            frame.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'q')
        {
            clearUp();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {
//        IMAGE_ID--;
    }

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

class Circle extends JComponent {

    protected Sticker sticker;
    protected JWindow frame;
    protected static int CIRCLE_OBJECTS = 0;
    private Image image;
    public boolean getIn = false;

    public Circle(Sticker s, String name)
    {
        ++CIRCLE_OBJECTS;
        sticker = s;
        frame = new JWindow();
        frame.setSize(100,100);
        frame.setAlwaysOnTop(true);
        if (name.equals("copy"))
            frame.setLocation((Sticker.SCREEN_WIDTH)-frame.getWidth()-(frame.getWidth()/4), (Sticker.SCREEN_HEIGHT/2)-frame.getHeight()-frame.getHeight());
        else if (name.equals("delete"))
            frame.setLocation((Sticker.SCREEN_WIDTH)-frame.getWidth()-(frame.getWidth()/4), (Sticker.SCREEN_HEIGHT/2)-frame.getHeight()+frame.getHeight()*2);
        frame.setBackground(new Color(0,0,0,1));
        frame.getContentPane().add(this);
    }

    public void exit()
    {
        this.frame.dispose();
    }

    public Point getAbsoluteLocation()
    {
        return this.frame.getLocation();
    }

    public void requestRepaint()
    {
        this.repaint();
    }

    public void setImage(Image image)
    {
        this.image = image;
    }

    public boolean isAlive()
    {
        return this.frame.isShowing();
    }

    public void showCircle()
    {
        frame.setVisible(true);
    }

    public void hideCircle()
    {
        frame.setVisible(false);
    }

    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (getIn)
            g2d.setColor(new Color(0,0,50,200));
        else
            g2d.setColor(new Color(0,0,50,33));
        g2d.fillOval(0,0,frame.getWidth(), frame.getHeight());
        g2d.drawImage(image,((frame.getWidth()/2)/2)/2+((frame.getWidth()/2)/2)/2/3,(frame.getHeight()/2/2)/2+((frame.getHeight()/2)/2)/2/3,null);
        g2d.dispose();
    }

}
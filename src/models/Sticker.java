package models;

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
    public SnippetController snippetController;
    public Circle circleCopy;
    public Circle circleDelete;
    public Rectangle copy_bounds;
    private BufferedImage image;
    private final JFrame frame;
    private final AWTEventListener listener;
    private final Border unpressedBorder = BorderFactory.createLineBorder(Color.WHITE, 0, true);
    private final Border pressedBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3, true);
    private int initX;
    private int initY;
    public Sticker(SnippetController sc)
    {
        listener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                KeyEvent evt = (KeyEvent)event;
                if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_S)
                {
                    Drawer.HOVER_SNIPPET = false;
                    frame.dispose();
                    snippetController.saveShot(image);
                }
            }
        };
        this.snippetController = sc;
        image = snippetController.getImage();
        this.frame = new JFrame();
        frame.setTitle(String.valueOf(++IMAGE_ID));
        frame.setIconImage(MainClass.ICON);
        frame.setSize(image.getWidth(), image.getHeight());
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(this);
        frame.setUndecorated(true);
        frame.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        this.setBorder(unpressedBorder);
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
        frame.addKeyListener(this);
        frame.addWindowListener(this);
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, KeyEvent.KEY_EVENT_MASK);

        circleCopy = new Circle(this);
        circleDelete = new Circle(this);
        circleCopy.setImage(MainClass.COPY);
        circleDelete.setImage(MainClass.DELETE);

    }

    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, null, 0, 0);

    }

    public BufferedImage getImage()
    {
        return image;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        initX = e.getX();
        initY = e.getY();
        this.setBorder(pressedBorder);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.setBorder(unpressedBorder);
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        int xMouse = e.getXOnScreen();
        int yMouse = e.getYOnScreen();
        frame.setLocation(xMouse-initX, yMouse-initY);
        this.setBorder(pressedBorder);

        if (xMouse >= SCREEN_WIDTH-RIGHT_SIDE_REGION)
        {
            copy_bounds = circleCopy.getBounds();
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
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'q')
        {
            IMAGE_ID--;
            Circle.CIRCLE_OBJECTS -= 2;
            if (!WideKeyListener.FROM_WIDE_KEY_LISTENER)
            {
                this.frame.dispose();
                this.snippetController.homeGui.Frame.setVisible(true);
            }
            else
            {
                this.frame.dispose();
            }
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        IMAGE_ID--;
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

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

    public Circle(Sticker s)
    {
        ++CIRCLE_OBJECTS;
        sticker = s;
        frame = new JWindow();
        frame.setSize(100,100);
        frame.setAlwaysOnTop(true);
        if (CIRCLE_OBJECTS == 1)
            frame.setLocation((Sticker.SCREEN_WIDTH)-frame.getWidth()-(frame.getWidth()/4), (Sticker.SCREEN_HEIGHT/2)-frame.getHeight()-frame.getHeight() );
        else if (CIRCLE_OBJECTS == 2)
            frame.setLocation((Sticker.SCREEN_WIDTH)-frame.getWidth()-(frame.getWidth()/4), (Sticker.SCREEN_HEIGHT/2)-frame.getHeight()+frame.getHeight()*2 );

//        frame.setUndecorated(true);
        frame.setBackground(new Color(0,0,0,1));

        frame.getContentPane().add(this);
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
        g2d.setColor(new Color(0,0,50,33));
        g2d.fillOval(0,0,frame.getWidth(), frame.getHeight());
        g2d.drawImage(image,((frame.getWidth()/2)/2)/2+((frame.getWidth()/2)/2)/2/3,(frame.getHeight()/2/2)/2+((frame.getHeight()/2)/2)/2/3,null);
        g2d.dispose();
    }

}
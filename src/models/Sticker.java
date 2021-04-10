package models;

import controllers.SnippetController;
import views.MainClass;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Sticker extends JComponent implements MouseListener, MouseMotionListener, KeyListener {
    public SnippetController snippetController;
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
                if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_E)
                {
                    new Editer(getCurrentObject(), getSnippetController()).init();
                    frame.dispose();
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, KeyEvent.KEY_EVENT_MASK);
        this.snippetController = sc;
        image = snippetController.getImage();
        this.frame = new JFrame();
        frame.setIconImage(MainClass.ICON);
        frame.setSize(image.getWidth(), image.getHeight());
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(this);
        frame.setUndecorated(true);
        frame.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        System.out.println("Image yHeight: " + image.getHeight());
        this.setBorder(unpressedBorder);
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
        frame.addKeyListener(this);
    }

    public SnippetController getSnippetController()
    {
        return this.snippetController;
    }

    public Sticker getCurrentObject()
    {
        return this;
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
        int yImageMouse = e.getY();
        System.out.println("Screen Y: " + yMouse);
        System.out.println("y: "+ yImageMouse);
        frame.setLocation(xMouse-initX, yMouse-initY);
        this.setBorder(pressedBorder);
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'q')
        {
            this.frame.dispose();
            this.snippetController.homeGui.Frame.setVisible(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}

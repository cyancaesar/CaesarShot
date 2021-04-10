package models;

import controllers.SnippetController;
import views.MainClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class Editer extends JComponent implements MouseListener, MouseMotionListener {
    public Sticker sticker;
    public SnippetController snippetController;
    private BufferedImage image;
    private AWTEventListener listener;
    private JFrame frame;
    public Graphics2D g2dMaster;

    public int baseX;
    public int baseY;
    public int xMouse;
    public int yMouse;
    private int finalX;
    private int finalY;
    public int recWidth;
    public int recHeight;
    public int x;
    public int y;
    public int yScreen;
    public int xScreen;

    public Editer(Sticker s, SnippetController sc)
    {
        this.sticker = s;
        this.snippetController = sc;
    }

    public void init()
    {
        image = sticker.getImage();
        this.frame = new JFrame();
        frame.setIconImage(MainClass.ICON);
        frame.setSize(image.getWidth(), image.getHeight());
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(this);
        frame.setUndecorated(true);
        frame.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0 ,this);
        g2d.setColor(Color.MAGENTA);
        g2d.fillOval(xMouse, yMouse, 20, 20);
        g2d.dispose();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        xMouse = e.getX();
        yMouse = e.getY();
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        xMouse = e.getX();
        yMouse = e.getY();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}
}

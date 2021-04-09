package models;

import controllers.SnippetController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.*;

public class Drawer extends JComponent implements MouseMotionListener, MouseListener, KeyListener {
    public SnippetController snippetController;
    private final JFrame frame;
    private Color color = new Color(255, 165, 0, 30);
    private final AWTEventListener listener;
    public static boolean EXIT_MARKER = false;
    public static boolean HOVER_SNIPPET = false;
    private int baseX;
    private int baseY;
    private int mouseX;
    private int mouseY;
    private int finalX;
    private int finalY;

    public Drawer(SnippetController snippetController)
    {
        this.snippetController = snippetController;
        listener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                KeyEvent evt = (KeyEvent)event;
                if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_S)
                {
                    if (!(baseX == finalX && baseY == finalY))
                    {
                        snippetController.setCoordinates(Math.min(baseX,finalX), Math.min(baseY, finalY), Math.max(baseX, finalX), Math.max(baseY, finalY));
                        frame.dispose();
                    }
                }
                else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_H)
                {
                    if (!(baseX == finalX && baseY == finalY))
                    {
                        Drawer.HOVER_SNIPPET = true;
                        snippetController.setCoordinates(Math.min(baseX,finalX), Math.min(baseY, finalY), Math.max(baseX, finalX), Math.max(baseY, finalY));
                        frame.dispose();
                    }
                }
                else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    mouseX = baseX;
                    mouseY = baseY;
                    finalX = baseX;
                    finalY = baseY;
                    repaint();
                }
                else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getKeyCode() == KeyEvent.VK_Q)
                {
                    Drawer.EXIT_MARKER = true;
                    frame.dispose();
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, KeyEvent.KEY_EVENT_MASK);
        frame = new JFrame();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addMouseMotionListener(this);
        frame.addMouseListener(this);
        frame.addKeyListener(this);
        frame.addWindowListener(this.snippetController);
        frame.getContentPane().add(this);
        frame.setUndecorated(true);
        frame.setBackground(new Color(33,33,33,66));
//        frame.setOpacity((float)0.3);
        frame.setVisible(true);

    }

    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        int recWidth = Math.abs(mouseX - baseX);
        int recHeight = Math.abs(mouseY - baseY);
        int x = Math.min(mouseX, baseX);
        int y = Math.min(mouseY, baseY);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(12.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.fillRect(x, y, recWidth, recHeight);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        baseX = e.getX();
        baseY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        finalX = e.getX();
        finalY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar())
        {
            case '1':
                color = new Color(255, 165, 0, 30);
                break;
            case '2':
                color = Color.YELLOW;
                break;
            case '3':
                color = Color.RED;
                break;
            case '4':
                color = Color.BLUE;
                break;
            case '5':
                color = Color.GREEN;
                break;
            case '6':
                color = Color.PINK;
                break;
            case '7':
                color = Color.LIGHT_GRAY;
                break;
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}

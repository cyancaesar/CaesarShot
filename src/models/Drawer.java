package models;

import controllers.SnippetController;
import javax.swing.JFrame;
import javax.swing.JComponent;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.*;

public class Drawer extends JComponent implements MouseMotionListener, MouseListener, KeyListener {
    public SnippetController snippetController;
    private final JFrame frame;
    private Color color = Color.ORANGE;
    private final AWTEventListener listener;
    private int baseX;
    private int baseY;
    private int mouseX;
    private int mouseY;
    private int x;
    private int y;
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
                        snippetController.setCoordinates(baseX, baseY, finalX, finalY);
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
            }
        };
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
        frame.setOpacity((float)0.3);
        frame.setVisible(true);
    }

    public void paintComponent(Graphics g)
    {
        Graphics2D g2D = (Graphics2D)g;
        int recWidth = Math.abs(mouseX - baseX);
        int recHeight = Math.abs(mouseY - baseY);
        x = Math.min(mouseX, baseX);
        y = Math.min(mouseY, baseY);
        g2D.setColor(color);
        g2D.fillRect(x, y, recWidth, recHeight);
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
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, KeyEvent.KEY_EVENT_MASK);
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
            case 'o':
                color = Color.ORANGE;
                break;
            case 'y':
                color = Color.YELLOW;
                break;
            case 'r':
                color = Color.RED;
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

package models;

import controllers.SnippetController;
import views.MainClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class Drawer extends JComponent implements MouseMotionListener, MouseListener, KeyListener {
    public SnippetController snippetController;
    private final JFrame frame;
    private Color color = new Color(255, 165, 0, 200);
    private final AWTEventListener listener;
    public static boolean EXIT_MARKER = false;
    public static boolean HOVER_SNIPPET = false;
    public static boolean COPY_IMAGE = false;
    private final static String INSTRUCTION_FONT_PATH = "D:\\Browser Downloads\\Fonts\\instruction\\Instruction.otf";
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
                        MainClass.playSound("wow.wav");
                        Drawer.HOVER_SNIPPET = false;
                        snippetController.setCoordinates(Math.min(baseX,finalX), Math.min(baseY, finalY), Math.max(baseX, finalX), Math.max(baseY, finalY));
                        frame.dispose();
                    }
                }
                else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_H)
                {
                    if (!(baseX == finalX && baseY == finalY))
                    {
                        MainClass.playSound("perfect.wav");
                        Drawer.HOVER_SNIPPET = true;
                        snippetController.setCoordinates(Math.min(baseX,finalX), Math.min(baseY, finalY), Math.max(baseX, finalX), Math.max(baseY, finalY));
                        frame.dispose();
                    }
                }
                else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_C)
                {
                    if (!(baseX == finalX && baseY == finalY))
                    {
//                        MainClass.playSound("perfect.wav");
                        Drawer.COPY_IMAGE = true;
                        snippetController.setCoordinates(Math.min(baseX,finalX), Math.min(baseY, finalY), Math.max(baseX, finalX), Math.max(baseY, finalY));
                        frame.dispose();
                    }
                }
                else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    xMouse = baseX;
                    yMouse = baseY;
                    finalX = baseX;
                    finalY = baseY;
                    repaint();
                }
                else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getKeyCode() == KeyEvent.VK_Q)
                {
                    Drawer.HOVER_SNIPPET = false;
                    Drawer.EXIT_MARKER = true;
                    frame.dispose();
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, KeyEvent.KEY_EVENT_MASK);
        frame = new Frame(this);
        frame.setIconImage(MainClass.ICON);
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
        recWidth = Math.abs(xMouse - baseX);
        recHeight = Math.abs(yMouse - baseY);
        x = Math.min(xMouse, baseX);
        y = Math.min(yMouse, baseY);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(INSTRUCTION_FONT_PATH));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        assert font != null;
        g2d.setFont(font.deriveFont(16f));
        g2d.setColor(Color.WHITE);
        g2d.drawString("CTRL + H: Make Sticky Snippet", (frame.getWidth()/2)-150, 20);
        g2d.drawString("CTRL + S: Save Snippet", (frame.getWidth()/2)-150, 40);
        g2d.drawString("Esc: Reset Area", (frame.getWidth()/2)-150, 60);
        g2d.drawString("Q: Quit", (frame.getWidth()/2)-150, 80);

        g2d.setColor(color);
        if (!(finalX == baseX))
        {
            g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
            g2d.drawRect(x, y, recWidth, recHeight);
        }
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
        xMouse = e.getX();
        yMouse = e.getY();
        xScreen = e.getXOnScreen();
        yScreen = e.getYOnScreen();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar())
        {
            case '1':
                color = new Color(255, 165, 0, 200);
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
class Frame extends JFrame
{
    Drawer drawerPanel;
    public Frame(Drawer panel)
    {
        this.drawerPanel = panel;
        System.out.println("Frame from JFrame");
    }

//    @Override
//    public void paint(Graphics g) {
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        int xScreenSize = screenSize.width;
//        int yScreenSize = screenSize.height;
//        int yTopDistant = drawerPanel.yScreen - drawerPanel.baseY;
//        Point yPoint = new Point();
//        yPoint.setLocation(0, yScreenSize);
//        g.setColor(Color.MAGENTA);
//        g.fillRect(0,0, xScreenSize,yTopDistant);
//    }
}

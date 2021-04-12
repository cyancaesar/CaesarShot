package models;

import controllers.SnippetController;
import views.MainClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.File;

public class Drawer extends JComponent implements MouseMotionListener, MouseListener, KeyListener {
    public SnippetController snippetController;
    private final JFrame frame;
    private Color color = new Color(255, 165, 0, 200);
    public static boolean EXIT_MARKER = false;
    public static boolean HOVER_SNIPPET = false;
    public static boolean COPY_IMAGE = false;
    private final static String INSTRUCTION_FONT_PATH = "\\assets\\Instruction.otf";
    private final static String DIMENSION_FONT_PATH = "\\assets\\CaviarDreams_Bold.ttf";
    private static Font INSTRUCTION_FONT = null;
    private static Font DIMENSION_FONT = null;
    private static final float[] DASH_OPTION = new float[] {5.0f};
    private static final BasicStroke DASHED_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 20.0f, DASH_OPTION, 0.0f);
    private int xBase;
    private int yBase;
    private int xMouse;
    private int yMouse;
    private int xFinal;
    private int yFinal;
    private int recWidth;
    private int recHeight;
    private int x;
    private int y;
    private int xScreen;
    private int yScreen;

    public Drawer(SnippetController snippetController)
    {
        setFonts();
        this.snippetController = snippetController;
        AWTEventListener listener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                KeyEvent evt = (KeyEvent) event;
                if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_S) {
                    if (!(xBase == xFinal && yBase == yFinal)) {
                        MainClass.playSound("cut.wav");
                        Drawer.HOVER_SNIPPET = false;
                        snippetController.setCoordinates(Math.min(xBase, xFinal), Math.min(yBase, yFinal), Math.max(xBase, xFinal), Math.max(yBase, yFinal));
                        frame.dispose();
                    }
                } else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_H) {
                    if (!(xBase == xFinal && yBase == yFinal)) {
                        MainClass.playSound("cut.wav");
                        Drawer.HOVER_SNIPPET = true;
                        snippetController.setCoordinates(Math.min(xBase, xFinal), Math.min(yBase, yFinal), Math.max(xBase, xFinal), Math.max(yBase, yFinal));
                        frame.dispose();
                    }
                } else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_C) {
                    if (!(xBase == xFinal && yBase == yFinal)) {
                        MainClass.playSound("cut.wav");
                        Drawer.HOVER_SNIPPET = false;
                        Drawer.COPY_IMAGE = true;
                        Drawer.EXIT_MARKER = false;
                        snippetController.setCoordinates(Math.min(xBase, xFinal), Math.min(yBase, yFinal), Math.max(xBase, xFinal), Math.max(yBase, yFinal));
                        frame.dispose();
                    }
                } else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    xMouse = xBase;
                    yMouse = yBase;
                    xFinal = xBase;
                    yFinal = yBase;
                    repaint();
                } else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getKeyCode() == KeyEvent.VK_Q) {
                    Drawer.HOVER_SNIPPET = false;
                    Drawer.EXIT_MARKER = true;
                    frame.dispose();
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, KeyEvent.KEY_EVENT_MASK);
        frame = new JFrame();
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
        frame.setOpacity(0.7f);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
    }

    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        recWidth = Math.abs(xMouse - xBase);
        recHeight = Math.abs(yMouse - yBase);
        x = Math.min(xMouse, xBase);
        y = Math.min(yMouse, yBase);
        Rectangle2D rectangleNotToDrawIn = new Rectangle2D.Double(x,y,recWidth,recHeight);
        Area outside = calculateRectOutside(rectangleNotToDrawIn);
        g2d.setPaint(new Color(0,0,0,100));
        g2d.setClip(outside);
        g2d.fillRect(0,0,getWidth(),getHeight());

        g2d.setFont(INSTRUCTION_FONT.deriveFont(16f));
        g2d.setColor(Color.WHITE);
        g2d.drawString("CTRL + H: Make Sticky Snippet", (frame.getWidth()/2)-150, 20);
        g2d.drawString("CTRL + S: Save Snippet", (frame.getWidth()/2)-150, 40);
        g2d.drawString("Esc: Reset Area", (frame.getWidth()/2)-150, 60);
        g2d.drawString("Q: Quit", (frame.getWidth()/2)-150, 80);

        if (!(xFinal == xBase))
        {
            g2d.setPaint(new Color(255, 165, 0, 0));
            g2d.fillRect(0,0, getWidth(), getHeight());
            g2d.setStroke(DASHED_STROKE);
            g2d.setColor(Color.ORANGE);
            g2d.drawRect(x, y, recWidth, recHeight);
            g2d.setFont(DIMENSION_FONT.deriveFont(12f));
            String dim = recWidth + "x" + recHeight;
            g2d.setColor(Color.WHITE);
            g2d.drawString(dim, x, y-10);
        }
    }

    private Area calculateRectOutside(Rectangle2D r) {
        Area outside = new Area(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        outside.subtract(new Area(r));
        return outside;
    }

    private void setFonts()
    {
        try
        {

            INSTRUCTION_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(MainClass.CURRENT_DIR.getFile() + INSTRUCTION_FONT_PATH));
            DIMENSION_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(MainClass.CURRENT_DIR.getFile() + DIMENSION_FONT_PATH));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        xBase = e.getX();
        yBase = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        xFinal = e.getX();
        yFinal = e.getY();
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

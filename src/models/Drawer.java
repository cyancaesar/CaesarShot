package models;

import controllers.SnippetController;
import views.Home;
import views.MainClass;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class Drawer extends JPanel implements MouseMotionListener, MouseListener, KeyListener, ActionListener, MouseWheelListener {
    public SnippetController snippetController;
    public JFrame frame;
    public static Action action;
    public static Tool tool = Tool.DEFAULT;
    private static boolean CLEAR = true;
    private static final String[] INSTRUCTION_1 = new String[] {"Sticky Snippet", "[CTRL + H]"};
    private static final String[] INSTRUCTION_2 = new String[] {"Save Snippet", "[CTRL + S]"};
    private static final String[] INSTRUCTION_3 = new String[] {"Copy Snippet", "[CTRL + C]"};
    private static final String[] INSTRUCTION_4 = new String[] {"Reset Area", "[ESC]"};
    private static final String[] INSTRUCTION_5 = new String[] {"Quit", "[Q]"};
    private static final String[] INSTRUCTION_6 = new String[] {"Undo", "[CTRL + Z]"};
    private static  final String[] STATE = new String[] {"State", "Default"};
    private static final String FONT_NAME = "Segoe UI";
    private static final float[] DASH_OPTION = new float[] {2.0f};
    private static final BasicStroke DASHED_STROKE = new BasicStroke(
            2.5f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f,
            DASH_OPTION,
            10.0f);
    private final static int RIGHT_CLICK = 4096;
    private final static int LEFT_CLICK = 1024;
    private static boolean IS_LEFT_CLICKED = false;
    private static boolean IS_DRAGGING = false;

    public static BufferedImage screenImage;

    private Point startPoint = new Point(0,0);
    private Point currentPoint = new Point(0,0);
    private Point finalPoint = new Point(0,0);
    private int xMin;
    private int yMin;
    private int xMax;
    private int yMax;
    private int x;
    private int y;
    private int paintWidth;
    private int paintHeight;

    private Point oPaint;
    private Point cPaint;
    private Point fPaint;

    private static int STROKE_WIDTH = 3;
    private Vector<Tool> paintingType = new Vector<>();
    /*
     * Painting points
     * paintCurves: Draw tool
     * lines: Line tool
     */
    private List<List<Point>> brushCurves = new ArrayList<>();

    private List<Point> temp_brushCurve = null;
    private List<List<Point>> linePoints = new ArrayList<>();
    private List<Point> temp_linePoint = null;
    private List<List<Point>> rectanglePoints = new ArrayList<>();
    private List<Point> temp_rectanglePoints = null;

    private JPanel toolsPanel;
    private JButton brushTool;
    private JButton lineTool;
    private JButton rectangleTool;
    private JButton removeTool;
    private JButton undoTool;

    public Drawer(SnippetController snippetController, BufferedImage image)
    {
        JFrame.setDefaultLookAndFeelDecorated(false);
        this.snippetController = snippetController;
        screenImage = image;

        /*
         * Toolbar creation
         */
        setupTools();
        /*
         * Keyboard listener for actions
         */
        AWTEventListener listener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event)
            {
                KeyEvent evt = (KeyEvent) event;
                // When Ctrl + S is pressed
                // Save the snippet to file
                if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_S)
                {
                    if (startPoint != finalPoint)
                    {
                        if (Home.SOUND)
                            MainClass.playSound("cut2.wav");
                        Drawer.tool = Tool.DEFAULT;
                        Drawer.action = Action.SAVE_IMAGE;
                        snippetController.shotSnippet(xMin, yMin, xMax, yMax);
                        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                        frame.dispose();
                    }
                }
                // When Ctrl + H is pressed
                // Show the snippet to display
                else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_H)
                {
                    if (startPoint != finalPoint)
                    {
                        if (Home.SOUND)
                             MainClass.playSound("cut2.wav");
                        Drawer.tool = Tool.DEFAULT;
                        Drawer.action = Action.HOVER_SNIPPET;
                        snippetController.shotSnippet(xMin, yMin, xMax, yMax);
                        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                        frame.dispose();
                    }
                }
                // When Ctrl + C is pressed
                // Copy the snippet to clipboard
                else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_C)
                {
                    if (startPoint != finalPoint)
                    {
                        if (Home.SOUND)
                            MainClass.playSound("cut2.wav");
                        Drawer.tool = Tool.DEFAULT;
                        Drawer.action = Action.COPY_IMAGE;
                        snippetController.shotSnippet(xMin, yMin, xMax, yMax);
                        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                        frame.dispose();
                    }
                }
                // When Esc is pressed
                // Reset the selection region
                else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    Drawer.tool = Tool.DEFAULT;
                    frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                    currentPoint = startPoint;
                    finalPoint = startPoint;
                    paintingType.clear();
                    brushCurves.clear();
                    linePoints.clear();
                    rectanglePoints.clear();
                    CLEAR = true;
                    STATE[1] = "Default";
                    hideTools();
                    repaint();
                }
                // When Q is pressed
                // Exit the drawing view
                else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getKeyCode() == KeyEvent.VK_Q)
                {
                    currentPoint = startPoint;
                    finalPoint = startPoint;
                    CLEAR = true;
                    Drawer.tool = Tool.DEFAULT;
                    Drawer.action = Action.EXIT;
                    repaint();
                    Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                    frame.dispose();
                }
                // When Ctrl + Z is pressed
                // Undo the last painting
                else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_Z)
                {
                    undoOperation();
                }
                else if (evt.getID() == KeyEvent.KEY_PRESSED && evt.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && evt.getKeyCode() == KeyEvent.VK_A)
                {
                    // Not implemented
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, KeyEvent.KEY_EVENT_MASK);

        // JFrame initialization
        frame = new JFrame();
        frame.setIconImages(MainClass.ICONS);
        frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        frame.setSize(MainClass.SCREEN_DIMENSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // All the listeners is attached to the panel
        frame.addMouseMotionListener(this);
        frame.addMouseListener(this);
        frame.addKeyListener(this);
        frame.addMouseWheelListener(this);
        frame.addWindowListener(snippetController);

        // Toolbar listeners is attached to the panel
        brushTool.addActionListener(this);
        lineTool.addActionListener(this);
        rectangleTool.addActionListener(this);
        undoTool.addActionListener(this);

        this.setLayout(null);
        frame.getContentPane().add(this);
        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        paintWidth = Math.abs(currentPoint.x - startPoint.x);
        paintHeight = Math.abs(currentPoint.y - startPoint.y);
        x = Math.min(currentPoint.x, startPoint.x);
        y = Math.min(currentPoint.y, startPoint.y);

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(screenImage, null, 0,0);

        Rectangle2D rectangleNotToDrawIn = new Rectangle2D.Double(x, y, paintWidth, paintHeight);
        Area outside = calculateRectOutside(rectangleNotToDrawIn);
        g2d.setPaint(new Color(0,0,0,150));
        g2d.setClip(outside);
        g2d.fillRect(0,0,getWidth(),getHeight());

        g2d.setFont(new Font(FONT_NAME, Font.BOLD, 16));
        FontMetrics textMetrics = g2d.getFontMetrics();
        g2d.setColor(Color.WHITE);
        g2d.drawString(INSTRUCTION_1[0], 50, 50);
        g2d.drawString(INSTRUCTION_2[0], 50, 75);
        g2d.drawString(INSTRUCTION_3[0], 50, 100);
        g2d.drawString(INSTRUCTION_4[0], 50, 125);
        g2d.drawString(INSTRUCTION_5[0], 50, 150);
        g2d.drawString(INSTRUCTION_6[0], 50, 175);
        g2d.drawString(STATE[0], 50, 200);

        g2d.drawString(INSTRUCTION_1[1], 100+175-textMetrics.stringWidth(INSTRUCTION_1[1])/2, 50);
        g2d.drawString(INSTRUCTION_2[1], 100+175-textMetrics.stringWidth(INSTRUCTION_2[1])/2, 75);
        g2d.drawString(INSTRUCTION_3[1], 100+175-textMetrics.stringWidth(INSTRUCTION_3[1])/2, 100);
        g2d.drawString(INSTRUCTION_4[1], 100+175-textMetrics.stringWidth(INSTRUCTION_4[1])/2, 125);
        g2d.drawString(INSTRUCTION_5[1], 100+175-textMetrics.stringWidth(INSTRUCTION_5[1])/2, 150);
        g2d.drawString(INSTRUCTION_6[1], 100+175-textMetrics.stringWidth(INSTRUCTION_6[1])/2, 175);
        g2d.setColor(Color.GREEN);
        g2d.drawString(STATE[1], 100+175-textMetrics.stringWidth(STATE[1])/2, 200);
        g2d.setColor(Color.WHITE);

        Graphics2D gImage = (Graphics2D) g.create();
        gImage.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        gImage.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        gImage.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        gImage.setStroke(new BasicStroke(
                STROKE_WIDTH, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        gImage.setColor(Color.RED);

        for (List<Point> curve : brushCurves)
        {
            drawCurve(gImage, curve);
        }
        for (List<Point> line : linePoints)
        {
            gImage.drawLine(line.get(0).x, line.get(0).y, line.get(1).x, line.get(1).y);
        }
        for (List<Point> rectangle : rectanglePoints)
        {
            int xBase = rectangle.get(0).x;
            int yBase = rectangle.get(0).y;
            int xFinal = rectangle.get(rectangle.size()-1).x;
            int yFinal = rectangle.get(rectangle.size()-1).y;
            int width = Math.abs(xBase - xFinal);
            int height = Math.abs(yBase - yFinal);
            int xRec = Math.min(xBase, xFinal);
            int yRec = Math.min(yBase, yFinal);
            gImage.drawRect(xRec, yRec, width, height);
        }

        if (tool == Tool.BRUSH)
        {
            if (temp_brushCurve != null) {
                gImage.setColor(Color.CYAN);
                drawCurve(gImage, temp_brushCurve);
            }
        }
        else if (tool == Tool.LINE)
        {
            if (temp_linePoint != null)
            {
                gImage.drawLine(temp_linePoint.get(0).x, temp_linePoint.get(0).y, temp_linePoint.get(1).x, temp_linePoint.get(1).y);
            }
        }
        else if (tool == Tool.RECTANGLE)
        {
            if (temp_rectanglePoints != null)
            {
                int xBase = temp_rectanglePoints.get(0).x;
                int yBase = temp_rectanglePoints.get(0).y;
                int xFinal = temp_rectanglePoints.get(temp_rectanglePoints.size()-1).x;
                int yFinal = temp_rectanglePoints.get(temp_rectanglePoints.size()-1).y;
                int width = Math.abs(xBase - xFinal);
                int height = Math.abs(yBase - yFinal);
                int xRec = Math.min(xBase, xFinal);
                int yRec = Math.min(yBase, yFinal);
                gImage.drawRect(xRec, yRec, width, height);
            }
        }
//        gImage.dispose();

        if (!CLEAR)
        {
            g2d.drawRect(x-20, y-20, 20, 0); // Top left x
            g2d.drawRect(x-20, y-20, 0, 20); // Top left y
            g2d.drawRect(x+ paintWidth, y-20, 20, 0); // Top right x
            g2d.drawRect(x+ paintWidth +20, y-20, 0, 20); // Top right y
            g2d.drawRect(x+ paintWidth, y+ paintHeight +20, 20, 0); // Bottom right x
            g2d.drawRect(x+ paintWidth +20, y+ paintHeight, 0, 20); // Bottom right y
            g2d.drawRect(x-20, y+ paintHeight +20, 20, 0); // Bottom left x
            g2d.drawRect(x-20, y+ paintHeight, 0, 20); // Bottom left y

            g2d.setStroke(DASHED_STROKE);
            g2d.setColor(new Color(255,255,255,150));

            g2d.setFont(new Font(FONT_NAME, Font.BOLD, 16));
            String recDimensions = paintWidth + "x" + paintHeight;
            g2d.setColor(new Color(255,255,255,150));
            g2d.drawString(recDimensions, x-20, y-26);
        }
        gImage.dispose();
    }

    private void setupTools()
    {
        brushTool = new JButton();
        lineTool = new JButton();
        rectangleTool = new JButton();
        undoTool = new JButton();

        brushTool.setIcon(new ImageIcon(MainClass.BRUSH_TOOL));
        lineTool.setIcon(new ImageIcon(MainClass.LINE_TOOL));
        rectangleTool.setIcon(new ImageIcon(MainClass.RECTANGLE_TOOL));
        undoTool.setIcon(new ImageIcon(MainClass.UNDO_TOOL));
        brushTool.setFocusPainted(false);
        brushTool.setFocusable(false);

        lineTool.setFocusPainted(false);
        lineTool.setFocusable(false);
        rectangleTool.setFocusPainted(false);
        rectangleTool.setFocusable(false);
        undoTool.setFocusPainted(false);
        undoTool.setFocusable(false);
    }

    private void showTools()
    {
        this.add(brushTool);
        this.add(lineTool);
        this.add(rectangleTool);
        this.add(undoTool);

        int x = (this.x + paintWidth /2)-(25/2);
        int y = (this.y + paintHeight)+25;

        brushTool.setBounds(x-25, y, 25,25);
        lineTool.setBounds(x, y, 25,25);
        rectangleTool.setBounds(x+25, y, 25,25);
        undoTool.setBounds(x+50, y, 25,25);
        revalidate();
    }
    private void hideTools()
    {
        this.remove(brushTool);
        this.remove(lineTool);
        this.remove(rectangleTool);
        this.remove(undoTool);
        revalidate();
    }

    private void drawCurve(Graphics2D g2d, List<Point> curve)
    {
        if (curve.size() == 1)
        {
            int x1 = curve.get(0).x;
            int y1 = curve.get(0).y;
            g2d.drawLine(x1, y1, x1+1, y1+1);
        }
        for (int i = 1; i < curve.size(); ++i)
        {
            int x1 = curve.get(i - 1).x;
            int y1 = curve.get(i - 1).y;
            int x2 = curve.get(i).x;
            int y2 = curve.get(i).y;
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    private Area calculateRectOutside(Rectangle2D r) {
        Area outside = new Area(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        outside.subtract(new Area(r));
        return outside;
    }

    private void undoOperation()
    {
        if (paintingType.size() > 0)
        {
            Tool last = paintingType.remove(paintingType.size() - 1);
            switch (last)
            {
                case BRUSH:
                    brushCurves.remove(brushCurves.size() - 1);
                    break;
                case LINE:
                    linePoints.remove(linePoints.size() - 1);
                    break;
                case RECTANGLE:
                    rectanglePoints.remove(rectanglePoints.size() - 1);
                    break;
            }
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getModifiersEx() != LEFT_CLICK)
        {
            IS_LEFT_CLICKED = false;
            return;
        }
        if (tool == Tool.DEFAULT)
        {
            startPoint = e.getPoint();
        }
        else if (tool == Tool.LINE)
        {
            oPaint = e.getPoint();
            temp_linePoint = new ArrayList<>();
            temp_linePoint.add(oPaint);
        }
        else if (tool == Tool.BRUSH)
        {
            oPaint = e.getPoint();
            temp_brushCurve = new ArrayList<>();
            temp_brushCurve.add(oPaint);
            repaint();
        }
        else if (tool == Tool.RECTANGLE)
        {
            oPaint = e.getPoint();
            temp_rectanglePoints = new ArrayList<>();
            temp_rectanglePoints.add(oPaint);
        }
        CLEAR = false;
        IS_LEFT_CLICKED = true;
        IS_DRAGGING = false;
        hideTools();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!IS_LEFT_CLICKED)
            return;
        if (tool == Tool.DEFAULT)
        {
            currentPoint = e.getPoint();
        }
        else if (tool == Tool.LINE)
        {
            cPaint = e.getPoint();
            if (temp_linePoint.size() > 1)
                temp_linePoint.remove(temp_linePoint.size() - 1);
            temp_linePoint.add(cPaint);
        }
        else if (tool == Tool.BRUSH)
        {
            cPaint = e.getPoint();
            temp_brushCurve.add(cPaint);
        }
        else if (tool == Tool.RECTANGLE)
        {
            cPaint = e.getPoint();
            if (temp_rectanglePoints.size() > 1)
                temp_rectanglePoints.remove(temp_rectanglePoints.size() - 1);
            temp_rectanglePoints.add(cPaint);
        }
        IS_DRAGGING = true;
        repaint();
        oPaint = cPaint;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!IS_DRAGGING)
            return;
        if (!IS_LEFT_CLICKED)
            return;
        if (tool == Tool.DEFAULT)
        {
            finalPoint = e.getPoint();
        }
        else if (tool == Tool.LINE)
        {
            temp_linePoint.add(e.getPoint());
            Point startPoint = temp_linePoint.get(0);
            Point finalPoint = temp_linePoint.get(temp_linePoint.size()-1);
            temp_linePoint.clear();
            temp_linePoint.add(startPoint);
            temp_linePoint.add(finalPoint);
            linePoints.add(temp_linePoint);
            temp_linePoint = null;
            paintingType.add(tool);
        }
        else if (tool == Tool.BRUSH)
        {
            temp_brushCurve.add(e.getPoint());
            brushCurves.add(temp_brushCurve);
            temp_brushCurve = null;
            paintingType.add(tool);
        }
        else if (tool == Tool.RECTANGLE)
        {
            temp_rectanglePoints.add(e.getPoint());
            Point startPoint = temp_rectanglePoints.get(0);
            Point finalPoint = temp_rectanglePoints.get(temp_rectanglePoints.size() - 1);
            temp_rectanglePoints.clear();
            temp_rectanglePoints.add(startPoint);
            temp_rectanglePoints.add(finalPoint);
            rectanglePoints.add(temp_rectanglePoints);
            temp_rectanglePoints = null;
            paintingType.add(tool);
        }
        xMin = Math.min(startPoint.x, finalPoint.x);
        yMin = Math.min(startPoint.y, finalPoint.y);
        xMax = Math.max(startPoint.x, finalPoint.x);
        yMax = Math.max(startPoint.y, finalPoint.y);
        fPaint = finalPoint;
        repaint();
        showTools();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        int x = e.getX()+5;
//        int y = e.getY()+5;
//        cursorLabel.setBounds(x, y, 100, 50);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == brushTool)
        {
            if (tool != Tool.BRUSH)
            {
                tool = Tool.BRUSH;
                frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                STATE[1] = "Brush";
            }
            else
            {
                tool = Tool.DEFAULT;
                frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                STATE[1] = "Default";
            }
        }
        else if (actionEvent.getSource() == lineTool)
        {
            if (tool != Tool.LINE)
            {
                tool = Tool.LINE;
                frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                STATE[1] = "Line";
            }
            else
            {
                tool = Tool.DEFAULT;
                frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                STATE[1] = "Default";
            }
        }
        else if (actionEvent.getSource() == rectangleTool)
        {
            if (tool != Tool.RECTANGLE)
            {
                tool = Tool.RECTANGLE;
                frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                STATE[1] = "Rectangle";
            }
            else
            {
                tool = Tool.DEFAULT;
                frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                STATE[1] = "Default";
            }
        }
        else if (actionEvent.getSource() == undoTool)
        {
            undoOperation();
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
//        System.out.println(e.getWheelRotation());
//        if (!(STROKE_WIDTH <= 0))
//        {
//            STROKE_WIDTH += e.getWheelRotation();
//        }
        // Not implemented
    }
}

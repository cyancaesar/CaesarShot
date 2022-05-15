package models;

import controllers.SnippetController;
import views.Home;
import views.MainClass;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
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
    private static final String[] STATE = new String[] {"State", "Default"};
    private static final String FONT_NAME = "Segoe UI";
    private static final float[] DASH_OPTION = new float[] {2.0f};
    private static final BasicStroke DASHED_STROKE = new BasicStroke(
            2.5f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f,
            DASH_OPTION,
            10.0f);

    private static boolean IS_LEFT_PRESSED = false;
    private static boolean IS_DRAGGING = false;
    private static boolean IS_CROPPING = false;

    public static BufferedImage screenImage;

    private Point startPoint = new Point(0,0);
    private Point currentPoint = new Point(0,0);
    private Point finalPoint = new Point(0,0);

    private Point persistantStartPoint = new Point(0,0);
    private Point persistantCurrentPoint = new Point(0,0);

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

    private static int STROKE_WIDTH = 3;
    private final Vector<Tool> paintingType = new Vector<>();
    /*
     * Painting points
     * paintCurves: Draw tool
     * lines: Line tool
     */
    private final List<List<Point>> brushCurves = new ArrayList<>();
    private List<Point> temp_brushCurve = null;
    private Map<String, Integer> brush_properties = null;
    private final List<Map<String, Integer>> brush_properties_list = new ArrayList<>();


    private final List<List<Point>> linePoints = new ArrayList<>();
    private List<Point> temp_linePoint = null;
    private final List<List<Point>> rectanglePoints = new ArrayList<>();
    private List<Point> temp_rectanglePoints = null;

    private JButton brushTool;
    private JButton lineTool;
    private JButton rectangleTool;
    private JButton undoTool;

    public Drawer(SnippetController snippetController, BufferedImage image)
    {
        JFrame.setDefaultLookAndFeelDecorated(false);
        this.snippetController = snippetController;
        screenImage = image;
        setIgnoreRepaint(true);

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
                    startPoint = new Point(0,0);
                    currentPoint = new Point(0,0);
                    finalPoint = new Point(0,0);
                    persistantStartPoint = new Point(0,0);
                    persistantCurrentPoint = new Point(0,0);
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

        // Attach the listener
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, KeyEvent.KEY_EVENT_MASK);

        // JFrame initialization
        frame = new JFrame();
        frame.setIconImages(MainClass.ICONS);
        frame.setSize(MainClass.SCREEN_DIMENSION);
//        MainClass.GD[0].setFullScreenWindow(frame);
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
        // Initializing Graphics objects
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        Graphics2D g2dDrawing = (Graphics2D) g.create();

        if (IS_CROPPING)
        {
            paintWidth = Math.abs(currentPoint.x - startPoint.x);
            paintHeight = Math.abs(currentPoint.y - startPoint.y);
            x = Math.min(currentPoint.x, startPoint.x);
            y = Math.min(currentPoint.y, startPoint.y);
        }
        else
        {
            paintWidth = Math.abs(persistantCurrentPoint.x - persistantStartPoint.x);
            paintHeight = Math.abs(persistantCurrentPoint.y - persistantStartPoint.y);
            x = Math.min(persistantCurrentPoint.x, persistantStartPoint.x);
            y = Math.min(persistantCurrentPoint.y, persistantStartPoint.y);
        }

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2dDrawing.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2dDrawing.setColor(Color.ORANGE);

        // Draw the background frame
        g2d.drawImage(screenImage, null, 0,0);

        // Drawing the rectangle
        Rectangle2D rectangleNotToDrawIn = new Rectangle2D.Double(x, y, paintWidth, paintHeight);
        Area outside = calculateRectOutside(rectangleNotToDrawIn);
        g2d.setPaint(new Color(0,0,0,150));
        g2d.setClip(outside);
        g2d.fillRect(0,0,getWidth(),getHeight());

        // Instruction menu
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

        int counter = 0;
        for (List<Point> curve : brushCurves)
        {
            g2dDrawing.setStroke(new BasicStroke(brush_properties_list.get(counter).get("SIZE")));
            drawCurve(g2dDrawing, curve);
            counter++;
        }
        for (List<Point> line : linePoints)
        {
            g2dDrawing.drawLine(line.get(0).x, line.get(0).y, line.get(1).x, line.get(1).y);
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
            g2dDrawing.drawRect(xRec, yRec, width, height);
        }

        if (tool == Tool.BRUSH)
        {
            if (temp_brushCurve != null)
            {
                g2dDrawing.setStroke(new BasicStroke(brush_properties_list.get(counter).get("SIZE")));
                g2dDrawing.setColor(Color.CYAN);
                drawCurve(g2dDrawing, temp_brushCurve);
            }
        }
        else if (tool == Tool.LINE)
        {
            if (temp_linePoint != null)
            {
                g2dDrawing.drawLine(temp_linePoint.get(0).x, temp_linePoint.get(0).y, temp_linePoint.get(1).x, temp_linePoint.get(1).y);
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
                g2dDrawing.drawRect(xRec, yRec, width, height);
            }
        }

        // Reset the clip
        g2d.setClip(0,0, getWidth(), getHeight());
        // Drawing the corners & dimension
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
            g2d.setColor(new Color(255,255,255,255));
            g2d.drawString(recDimensions, x-20, y-26);
        }
        g2dDrawing.dispose();
    }
    ///////////////////////////
    // End of paintComponent
    //////////////////////////
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

    private Area calculateRectOutside(Rectangle2D r)
    {
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
    public void mousePressed(MouseEvent e)
    {
        if (!SwingUtilities.isLeftMouseButton(e))
        {
            IS_LEFT_PRESSED = false;
            return;
        }
        switch (tool)
        {
            case DEFAULT:
                startPoint = e.getPoint();
                break;
            case LINE:
                oPaint = e.getPoint();
                temp_linePoint = new ArrayList<>();
                temp_linePoint.add(oPaint);
                break;
            case BRUSH:
                oPaint = e.getPoint();
                temp_brushCurve = new ArrayList<>();
                temp_brushCurve.add(oPaint);
                brush_properties = new HashMap<>();
                brush_properties.put("SIZE", STROKE_WIDTH);
                brush_properties_list.add(brush_properties);
                repaint();
                break;
            case RECTANGLE:
                oPaint = e.getPoint();
                temp_rectanglePoints = new ArrayList<>();
                temp_rectanglePoints.add(oPaint);
                break;
        }
        CLEAR = false;
        IS_LEFT_PRESSED = true;
        IS_DRAGGING = false;
        hideTools();
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (!IS_LEFT_PRESSED)
            return;
        if (tool == Tool.DEFAULT)
        {
            IS_CROPPING = true;
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
    public void mouseReleased(MouseEvent e)
    {
        if (!IS_DRAGGING)
            return;
        if (!IS_LEFT_PRESSED)
            return;

        persistantStartPoint = startPoint;
        persistantCurrentPoint = currentPoint;

        if (tool == Tool.DEFAULT)
        {
            IS_CROPPING = true;
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
            brush_properties = null;
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
        repaint();
        showTools();
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        IS_CROPPING = false;
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
        if (STROKE_WIDTH >= 1)
        {
            if (STROKE_WIDTH <= 10)
                STROKE_WIDTH += e.getWheelRotation();
            else
                STROKE_WIDTH -= Math.abs(e.getWheelRotation());
        }
        else
            STROKE_WIDTH = 1;
    }
}

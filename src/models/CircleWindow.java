package models;

import easings.Easing;
import easings.EasingFunction;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.timing.interpolation.Interpolator;
import views.MainClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;

public class CircleWindow extends JPanel implements MouseListener
{
    private static int CIRCLES = 0;
    private final static int VERTICAL_CENTER = Sticker.SCREEN_HEIGHT / 2;
    public static boolean isShowing = false;
    private static final Color BASE_COLOR = new Color(36, 36, 36, 255);
    private static final Color HOVER_COLOR = new Color(62, 62, 62, 255);
    private static Color BODY_COLOR = BASE_COLOR;
    private static final Color BORDER_COLOR = new Color(112, 2, 146);
    public static boolean CLEAR_WINDOWS = false;

    private JWindow window;
    private Sticker sticker;
    private Ellipse2D circle;
    private int circleRadius;
    private float animate;
    private int duration;
    public Animator animatorIn;
    public Animator animatorOut;
    private TimingTarget targetIn;
    private TimingTarget targetOut;
    private Point startPoint;
    private Point finalPoint;
    private Image icon;
    public boolean inRange = false;

    public CircleWindow(Sticker sticker, Image icon)
    {
        CIRCLES++;
        this.sticker = sticker;
        this.icon = icon;
        init();
    }

    private void init()
    {
        window = new JWindow();
        circleRadius = 90;
        duration = 700;
        circle = new Ellipse2D.Double(0,0, circleRadius, circleRadius);

        startPoint = new Point(Sticker.SCREEN_WIDTH+300,VERTICAL_CENTER);
        finalPoint = new Point(420,VERTICAL_CENTER);

        if (CIRCLES == 1)
        {
            startPoint.y = VERTICAL_CENTER;
            finalPoint.y = VERTICAL_CENTER;
        }
        else if (CIRCLES == 2)
        {
            startPoint.y = VERTICAL_CENTER - 100;
            finalPoint.y = VERTICAL_CENTER - 100;
        }
        else
        {
            startPoint.y = VERTICAL_CENTER + 100;
            finalPoint.y = VERTICAL_CENTER + 100;
        }

        this.setBackground(new Color(0,0,0,0));
        window.setSize(circleRadius+5,circleRadius+5);
        window.setBackground(new Color(0,0,0,0));
        window.setAlwaysOnTop(true);
        window.setLocation(startPoint);
        window.add(this);
        window.setVisible(true);

        targetIn = new TimingTargetAdapter()
        {
            @Override
            public void timingEvent(float fraction)
            {
                animate = fraction;
                animateIn();
            }
        };
        targetOut = new TimingTargetAdapter()
        {
            @Override
            public void timingEvent(float fraction)
            {
                animate = fraction;
                animateOut();
            }
        };
        animatorIn = new Animator(duration, targetIn);
        animatorIn.setResolution(0);
        animatorOut = new Animator(duration+1000, targetOut);
        animatorOut.setResolution(0);

        setEasingIn(new Easing() {
            @Override
            public float easing(float f) {
                return EasingFunction.easeOutExpo(f);
            }
        });
        setEasingOut(new Easing() {
            @Override
            public float easing(float f) {
                return EasingFunction.easeOutBounce(f);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(BODY_COLOR);
        g2d.fill(circle);
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(circle);
        g2d.setClip(circle);
        int x = (circleRadius/2)-(icon.getWidth(null)/2);
        int y = (circleRadius/2)-(icon.getHeight(null)/2);
        g2d.drawImage(icon, x, y, null);
        g2d.dispose();
    }

    public void startIn()
    {
        isShowing = true;
        if (animatorOut.isRunning())
            animatorOut.stop();
        if (!animatorIn.isRunning())
            animatorIn.start();
    }

    public void startOut()
    {
        isShowing = false;
        if (animatorIn.isRunning())
            animatorIn.stop();
        if (!animatorOut.isRunning())
            animatorOut.start();
        if (CLEAR_WINDOWS)
            CIRCLES = 0;
    }

    public void setEasingIn(Easing easing)
    {
        animatorIn.setInterpolator(new Interpolator() {
            @Override
            public float interpolate(float f) {
                return easing.easing(f);
            }
        });
    }

    public void setEasingOut(Easing easing)
    {
        animatorOut.setInterpolator(new Interpolator() {
            @Override
            public float interpolate(float f) {
                return easing.easing(f);
            }
        });
    }

    private void animateIn()
    {
        int x =  startPoint.x - (int) (finalPoint.x * animate);
        window.setLocation(x, finalPoint.y);
    }

    private void animateOut()
    {
        int x =  (startPoint.x - finalPoint.x) + (int) (finalPoint.x * animate);
        window.setLocation(x, finalPoint.y);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e)
    {
        inRange = true;
        BODY_COLOR = HOVER_COLOR;
        repaint();
    }
    @Override
    public void mouseExited(MouseEvent e)
    {
        inRange = false;
        BODY_COLOR = BASE_COLOR;
        repaint();
    }
}

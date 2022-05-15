package views;

import com.formdev.flatlaf.*;
import controllers.FullscreenController;
import models.FileIO;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainClass {
    public final static String VERSION = "1.2";
    public final static ClassLoader loader = Thread.currentThread().getContextClassLoader();
    public final static Image ICON_16 = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/icon-16.png"));
    public final static Image ICON_32 = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/icon-32.png"));
    public final static Image ICON_64 = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/icon-64.png"));
    public final static Image ICON_128 = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/icon-128.png"));
    public final static ArrayList<Image> ICONS = new ArrayList<>();
    public final static Image SAVE = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/save.png"));
    public final static Image COPY = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/copy2.png"));
    public final static Image DELETE = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/delete2.png"));
    public final static Image BRUSH_TOOL = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/brush.png"));
    public final static Image RECTANGLE_TOOL = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/rectangle.png"));
    public final static Image LINE_TOOL = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/line.png"));
    public final static Image UNDO_TOOL = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/undo.png"));
    public final static Image COLOR_TOOL = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/color-wheel.png"));
    public final static GraphicsDevice[] GD = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    public static Dimension SCREEN_DIMENSION;

//    For Timing
    public static long START;
    public static long FINISH;
    public static long TIME_ELAPSED;


    public static Home MAIN_FRAME;

    public static void main(String[] args) {
        System.out.println("Java version: " + System.getProperty("java.version"));

        int SCREEN_WIDTH = 0;
        int SCREEN_HEIGHT = 0;
        for (GraphicsDevice gd : GD)
        {
            SCREEN_WIDTH += gd.getDisplayMode().getWidth();
            SCREEN_HEIGHT += gd.getDisplayMode().getHeight();
        }
        SCREEN_DIMENSION = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
        ICONS.add(ICON_16);
        ICONS.add(ICON_32);
        ICONS.add(ICON_64);
        ICONS.add(ICON_128);
        Runnable run = () -> {
            try
            {
                UIManager.put("Button.arc", 4);
                JFrame.setDefaultLookAndFeelDecorated(true);
                JDialog.setDefaultLookAndFeelDecorated(true);
                FileIO.getInstance().readConfig();
                IntelliJTheme.install(loader.getResourceAsStream("assets/twitch-dark.theme.json"));
                MAIN_FRAME = new Home();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        };
        SwingUtilities.invokeLater(run);
    }

    public synchronized static void playSound(final String url) {
            try {
                InputStream audioSrc = loader.getResourceAsStream("assets/" + url);
                assert audioSrc != null;
                InputStream audioSrcBuffer = new BufferedInputStream(audioSrc);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrcBuffer);
                AudioFormat format = audioStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.addLineListener(event -> {
                    LineEvent.Type type = event.getType();
                    if (type == LineEvent.Type.STOP) {
                        clip.close();
                        try
                        {
                            audioSrcBuffer.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                clip.open(audioStream);
                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
    }
}

package views;

import com.formdev.flatlaf.intellijthemes.FlatNordIJTheme;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainClass {
    public final static Image ICON = Toolkit.getDefaultToolkit().getImage(MainClass.class.getResource("..\\assets\\icon.png"));
    public final static Image ICON_16 = Toolkit.getDefaultToolkit().getImage(MainClass.class.getResource("..\\assets\\icon-16.png"));
    public final static Image ICON_40 = Toolkit.getDefaultToolkit().getImage(MainClass.class.getResource("..\\assets\\icon-100.png"));
    public final static Image COPY = Toolkit.getDefaultToolkit().getImage(MainClass.class.getResource("..\\assets\\copy.png"));
    public final static Image DELETE = Toolkit.getDefaultToolkit().getImage(MainClass.class.getResource("..\\assets\\delete.png"));
    public final static URL CURRENT_DIR = MainClass.class.getProtectionDomain().getCodeSource().getLocation();

    public static void main(String[] args) {
        Runnable run = () -> {
            try
            {
                FlatNordIJTheme.install();
                new Home();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        };
        SwingUtilities.invokeLater(run);
    }

    public static synchronized void playSound(final String url) {
        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                        MainClass.class.getResourceAsStream("..\\assets\\" + url));
                clip.open(inputStream);
                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }).start();
    }
}

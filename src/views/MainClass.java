package views;

import com.formdev.flatlaf.intellijthemes.FlatNordIJTheme;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class MainClass {
    public static ClassLoader loader = Thread.currentThread().getContextClassLoader();
    public final static Image ICON = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/icon.png"));
    public final static Image ICON_16 = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/icon-16.png"));
    public final static Image ICON_40 = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/icon-100.png"));
    public final static Image COPY = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/copy.png"));
    public final static Image DELETE = Toolkit.getDefaultToolkit().getImage(loader.getResource("assets/delete.png"));

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
                InputStream audioSrc = loader.getResourceAsStream("assets/" + url);
                assert audioSrc != null;
                InputStream audioSrcBuffer = new BufferedInputStream(audioSrc);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrcBuffer);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }).start();
    }
}

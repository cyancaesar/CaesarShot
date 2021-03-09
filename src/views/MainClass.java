package views;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.intellijthemes.FlatNordIJTheme;
import models.FileGuard;

import javax.swing.*;

public class MainClass {
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
    public static void sleep() throws InterruptedException {
        Thread.sleep(4000);
    }
}

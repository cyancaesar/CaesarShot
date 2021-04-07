package views;
import com.formdev.flatlaf.intellijthemes.FlatNordIJTheme;

import javax.swing.SwingUtilities;

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
}

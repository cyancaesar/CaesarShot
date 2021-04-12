package controllers;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import views.Home;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WideKeyListener implements NativeKeyListener {
    public static boolean FROM_WIDE_KEY_LISTENER = true;
    public Home home;
    public SnippetController snippetController;
    public WideKeyListener(Home h, SnippetController sc)
    {
        home = h;
        snippetController = sc;
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);
        logger.setUseParentHandlers(false);
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(this);
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_PRINTSCREEN) {
            FROM_WIDE_KEY_LISTENER = true;
            if (!snippetController.isDrawerAlive())
            {
                home.Frame.setVisible(false);
                snippetController.init();
            }
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {}

    public void nativeKeyTyped(NativeKeyEvent e) {}
}

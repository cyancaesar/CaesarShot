package controllers;


import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import models.Config;
import views.Home;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WideKeyListener implements NativeKeyListener {
    public static boolean FROM_WIDE_KEY_LISTENER = false;
    public Home home;
    public SnippetController snippetController;

    public WideKeyListener(Home home, SnippetController sc)
    {
        this.home = home;
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
//        System.out.println("Wide: " + e.getRawCode());
//        int rawCode = switch (e.getRawCode()) {
//            case 13 -> 10;
//            case 44 -> 154;
//            default -> e.getRawCode();
//        };
        int rawCode;
        switch (e.getRawCode())
        {
            case 13:
                rawCode = 10;
                break;
            case 44:
                rawCode = 154;
                break;
            default:
                rawCode = e.getRawCode();
        }
//        System.out.println("Enhanced Wide: " + rawCode);
        if (rawCode == Config.getInstance().getShortcutCode()) {
            FROM_WIDE_KEY_LISTENER = true;
            if (!snippetController.isDrawerAlive() && !this.home.frame.isVisible())
            {
                home.frame.setVisible(false);
                snippetController.init();
            }
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {}

    public void nativeKeyTyped(NativeKeyEvent e) {}
}

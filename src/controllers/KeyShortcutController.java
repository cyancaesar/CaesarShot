package controllers;

import models.Config;
import models.FileIO;
import views.Home;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class KeyShortcutController implements ActionListener, KeyEventDispatcher {
    Home homeGui;
    boolean keyFieldFocus = false;
    Integer keyCode;

    public KeyShortcutController(Home home)
    {
        this.homeGui = home;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(homeGui.getEditObject()))
        {
            if (!homeGui.getShortcutFieldEnable())
            {
                this.keyFieldFocus = true;
                homeGui.setShortcutFieldEnable(true);
            }
        }
        else if (actionEvent.getSource().equals(homeGui.getSetObject()))
        {
            if (homeGui.getShortcutFieldEnable())
            {
                this.keyFieldFocus = false;
                homeGui.setShortcutFieldEnable(false);
                Config.getInstance().setShortcutCode(this.keyCode);
                FileIO.getInstance().writeConfig(Config.getInstance());
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
//        System.out.println("Non Wide: " + keyEvent.getExtendedKeyCode());
        if (this.keyFieldFocus)
        {
            homeGui.setShortcut("");
            this.keyCode = keyEvent.getKeyCode();
            if (keyCode != 0)
            {
                String key = KeyEvent.getKeyText(keyEvent.getKeyCode());
                homeGui.setShortcut(key);
                return true;
            }
        }
        return false;
    }
}

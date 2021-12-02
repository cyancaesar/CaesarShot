package controllers;

import views.MainClass;

import javax.swing.*;

public class MessageDispatcher {
    protected void messageWriter(String msg, int type)
    {
        if (type == 0)
        {
            JFrame frame = new JFrame();
            frame.setIconImages(MainClass.ICONS);
            JOptionPane.showMessageDialog(frame, msg, "Error", type);
        }
        else if (type == 1)
        {
            JFrame frame = new JFrame();
            frame.setIconImages(MainClass.ICONS);
            JOptionPane.showMessageDialog(frame, msg, "Success", type);
        }
    }
    public static void messageWriter(String msg, String title, Icon icon)
    {
        JFrame frame = new JFrame();
        frame.setIconImages(MainClass.ICONS);
        JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.PLAIN_MESSAGE, icon);
    }
}

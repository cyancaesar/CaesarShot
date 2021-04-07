package controllers;

import javax.swing.*;

public class MessageDispatcher {
    protected void messageWriter(String msg, int type)
    {
        if (type == 0)
        {
            JOptionPane.showMessageDialog(null, msg, "Error", type);
        }
        else if (type == 1)
        {
            JOptionPane.showMessageDialog(null, msg, "Success", type);
        }
    }
}

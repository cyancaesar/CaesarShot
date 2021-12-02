package models;

import java.io.Serializable;

public class Config implements Serializable {
    private Integer shortcutCode = 36; // PRINT SCREEN key code
    private static Config CONFIG = null;

//    private Config() {}

    public void setShortcutCode(Integer code)
    {
        this.shortcutCode = code;
    }

    public Integer getShortcutCode()
    {
        return this.shortcutCode;
    }

    synchronized public static Config getInstance()
    {
        if (Config.CONFIG == null)
        {
            Config.CONFIG = new Config();
        }
        return Config.CONFIG;
    }
}

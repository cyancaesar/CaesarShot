package views;

import controllers.*;
import models.Config;
import models.FileIO;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Home {

    DirectoryController directoryController = new DirectoryController(this);
    FullscreenController fullscreenController = new FullscreenController(this);
    SnippetController snippetController = new SnippetController(this);
    WideKeyListener wideKeyListener = new WideKeyListener(this, snippetController);
    KeyShortcutController keyShortcutController = new KeyShortcutController(this);

    private final String defaultDirectory = FileIO.getInstance().getDefaultPath();

    public JFrame frame;
    private JPanel mainPanel;
    private JPanel centerPanel;
    private JPanel optionPanel;
    private JPanel actionPanel;
    private JPanel dirPanel;
    private JButton fullscreenButton;
    private JButton snippetButton;
    private JButton outputDirButton;
    private JButton editKeyButton;
    private JButton setKeyButton;

    private JTextField outputDir;
    private JTextField editKeyField;

    public static boolean OUTPUT_DIR_SET = false;
    public static boolean COPYRIGHT = true;
    public static boolean SOUND = true;

    public Home()
    {
        this.createAndShowGui();
    }

    private void createAndShowGui()
    {
        TrayIcon trayIcon;
        if (SystemTray.isSupported())
        {
            String about = "This utility is programmed by SirCaesar\n" +
                    "Thanks to: Raizen\n" +
                    "Contact: sircaesar@protonmail.ch\n" +
                    "Version: " + MainClass.VERSION;

            SystemTray systemTray = SystemTray.getSystemTray();
            Font trayFont = new Font("SansSerif", Font.PLAIN, 12);
            Image icon = MainClass.ICON_16;
            ActionListener actionListener = e -> frame.setVisible(true);
            PopupMenu popup = new PopupMenu();
            MenuItem exitBox = new MenuItem("Exit");
            MenuItem shotBox = new MenuItem("Take Shot");
            MenuItem aboutBox = new MenuItem("About");
            CheckboxMenuItem soundBox = new CheckboxMenuItem("Sounds");
            exitBox.setFont(trayFont);
            shotBox.setFont(trayFont);
            aboutBox.setFont(trayFont);
            soundBox.setFont(trayFont);
            soundBox.setState(SOUND);
            aboutBox.addActionListener(e -> MessageDispatcher.messageWriter(about, "About the author", new ImageIcon(MainClass.ICON_64)));
            exitBox.addActionListener(e -> System.exit(0));
            shotBox.addActionListener(snippetController);
            soundBox.addItemListener(e -> {
                int state = e.getStateChange();
                SOUND = state == 1;
                MainClass.playSound("cut2.wav");
            });

            popup.add(soundBox);
            popup.addSeparator();
            popup.add(shotBox);
            popup.add(aboutBox);
            popup.add(exitBox);

            trayIcon = new TrayIcon(MainClass.ICON_16, "CaesarShot", popup);
            try
            {
                systemTray.add(trayIcon);
            }
            catch (AWTException awtException)
            {
                awtException.printStackTrace();
            }
            trayIcon.addActionListener(actionListener);
        }

        frame = new JFrame("CaesarShot");
        frame.setIconImages(MainClass.ICONS);
        mainPanel = new JPanel(new BorderLayout(20, 10));
        dirPanel = new JPanel();
        dirPanel.setBorder(BorderFactory.createTitledBorder("Output Directory"));
        centerPanel = new JPanel();

        fullscreenButton = createButton("Fullscreen Shot");
        snippetButton = createButton("Area Shot");
        outputDirButton = createButton("Output");
        outputDir = new JTextField(defaultDirectory, 20);
        editKeyButton = createButton("Edit");
        setKeyButton = createButton("Bind Shortcut");
        editKeyField = new JTextField("");
        editKeyField.setText(KeyEvent.getKeyText(Config.getInstance().getShortcutCode()));
        Border padding = BorderFactory.createEmptyBorder(5,5,5,5);

        /*
         * Option Panel
         */
        optionPanel = new JPanel();
        optionPanel.setPreferredSize(new Dimension(200,90));
//        optionPanel.setBackground(Color.BLACK);
        BoxLayout optionBox = new BoxLayout(optionPanel, BoxLayout.Y_AXIS);

        JPanel editRow = new JPanel(new BorderLayout(10,0));
        editRow.add(editKeyButton, BorderLayout.WEST);
        editRow.add(editKeyField, BorderLayout.CENTER);

        editRow.setMaximumSize(new Dimension(optionPanel.getPreferredSize().width, 25));
        editKeyField.setMaximumSize(new Dimension(optionPanel.getPreferredSize().width, 25));
        editKeyButton.setMaximumSize(new Dimension(optionPanel.getPreferredSize().width, 25));
        setKeyButton.setMaximumSize(new Dimension(optionPanel.getPreferredSize().width, 25));
        setKeyButton.setAlignmentX(Button.CENTER_ALIGNMENT);

        optionPanel.setLayout(optionBox);
        optionPanel.add(editRow);
        optionPanel.add(Box.createVerticalGlue());
        optionPanel.add(setKeyButton);

        Border optionBorder = BorderFactory.createTitledBorder("Options");
        optionPanel.setBorder(BorderFactory.createCompoundBorder(optionBorder, padding));

        /*
         * Action Panel
         */
        actionPanel = new JPanel();
        actionPanel.setPreferredSize(new Dimension(200,55));
//        actionPanel.setBackground(Color.BLACK);
        BoxLayout actionBox = new BoxLayout(actionPanel, BoxLayout.Y_AXIS);
        fullscreenButton.setAlignmentX(Button.CENTER_ALIGNMENT);
        snippetButton.setAlignmentX(Button.CENTER_ALIGNMENT);
        fullscreenButton.setMaximumSize(new Dimension(actionPanel.getPreferredSize().width,25));
        snippetButton.setMaximumSize(new Dimension(actionPanel.getPreferredSize().width,25));
        actionPanel.setLayout(actionBox);
        actionPanel.add(fullscreenButton);
        actionPanel.add(Box.createVerticalGlue());
        actionPanel.add(snippetButton);
        Border actionBorder = BorderFactory.createTitledBorder("Actions");
        actionPanel.setBorder(BorderFactory.createCompoundBorder(actionBorder, padding));

        editKeyField.setEnabled(false);
        editKeyField.setHorizontalAlignment(SwingConstants.CENTER);
        outputDir.setEditable(false);
        outputDir.setFocusable(false);

        mainPanel.add(actionPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.SOUTH);
        mainPanel.add(optionPanel, BorderLayout.EAST);
        mainPanel.setBorder(new EmptyBorder(20,30,20,30));

        this.eventDispatcher();
        this.initDirectoryPanel();
        this.initCenterPanel();
        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JButton createButton(String Text)
    {
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        JButton btn = new JButton(Text);
        btn.setFont(font);
        btn.setFocusable(false);
        btn.setFocusPainted(false);
        return btn;
    }

    private void initDirectoryPanel()
    {
        GroupLayout grp = new GroupLayout(dirPanel);
        dirPanel.setLayout(grp);
        grp.setAutoCreateGaps(true);
        grp.setAutoCreateContainerGaps(true);
        grp.setVerticalGroup(
                grp.createParallelGroup().addComponent(outputDirButton).addComponent(outputDir)
        );
        grp.setHorizontalGroup(
                grp.createSequentialGroup().addComponent(outputDirButton).addComponent(outputDir)
        );
        grp.linkSize(SwingUtilities.VERTICAL, outputDirButton, outputDir);
    }

    private void initCenterPanel()
    {
        GroupLayout grp = new GroupLayout(centerPanel);
        centerPanel.setLayout(grp);
        grp.setAutoCreateGaps(true);
        grp.setAutoCreateContainerGaps(true);
        grp.setVerticalGroup(
                grp.createSequentialGroup().addComponent(dirPanel)
        );
        grp.setHorizontalGroup(
                grp.createParallelGroup().addComponent(dirPanel)
        );
    }

    private void eventDispatcher()
    {
        outputDirButton.addActionListener(directoryController);
        fullscreenButton.addActionListener(fullscreenController);
        snippetButton.addActionListener(snippetController);
        editKeyButton.addActionListener(keyShortcutController);
        setKeyButton.addActionListener(keyShortcutController);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyShortcutController);
    }

    public void setOutputDir(String Path) {
        OUTPUT_DIR_SET = true;
        outputDir.setText(Path);
    }

    public String getOutputDir()
    {
        return outputDir.getText();
    }

    public void setShortcutFieldEnable(boolean state)
    {
        this.editKeyField.setEnabled(state);
        this.editKeyField.setFocusable(state);
        if (state)
        {
            this.editKeyField.requestFocus();
        }
    }

    public boolean getShortcutFieldEnable()
    {
        return this.editKeyField.isEnabled();
    }

    public void setShortcut(String key)
    {
        this.editKeyField.setText(key);
    }

    public JButton getEditObject()
    {
        return this.editKeyButton;
    }

    public JButton getSetObject()
    {
        return this.setKeyButton;
    }

}

package views;

import controllers.DirectoryController;
import controllers.FullscreenController;
import controllers.SnippetController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.SystemTray;

public class Home {

    DirectoryController directoryController = new DirectoryController(this);
    FullscreenController fullscreenController = new FullscreenController(this);
    SnippetController snippetController = new SnippetController(this);

    public JFrame Frame;
    private JPanel MainPanel;
    private JPanel CenterPanel;
    private JPanel DirectoryPanel;

    private JButton FullscreenButton;
    private JButton SnippetButton;
    private JButton OutputDirectoryButton;

    private JTextField OutputDirectory;

    public Home()
    {
        this.createAndShowGui();
    }
    private void createAndShowGui()
    {
        if (SystemTray.isSupported())
        {
            System.out.println("Supported");
        }
        Frame = new JFrame("CaesarShot");
        Frame.setIconImage(MainClass.ICON);
        MainPanel = new JPanel(new BorderLayout(5, 10));
        DirectoryPanel = new JPanel();
        DirectoryPanel.setBorder(BorderFactory.createTitledBorder("Output Directory"));

        CenterPanel = new JPanel();

        FullscreenButton = createButton("Fullscreen Shot");
        SnippetButton = createButton("Area Shot");
        OutputDirectoryButton = createButton("Output");


        OutputDirectory = new JTextField("...", 20);
        OutputDirectory.setEditable(false);
        OutputDirectory.setFocusable(false);

        MainPanel.add(FullscreenButton, BorderLayout.NORTH);
        MainPanel.add(SnippetButton, BorderLayout.CENTER);
        MainPanel.add(CenterPanel, BorderLayout.SOUTH);
        MainPanel.setBorder(new EmptyBorder(20,30,20,30));

        this.eventDispatcher();
        this.initDirectoryPanel();
        this.initCenterPanel();
        Frame.add(MainPanel);
        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Frame.pack();
        Frame.setResizable(false);
        Frame.setLocationRelativeTo(null);
        Frame.setVisible(true);
    }
    private static JButton createButton(String Text)
    {
        JButton Button = new JButton(Text);
        Button.setFocusable(false);
        return Button;
    }
    private void initDirectoryPanel()
    {
        GroupLayout Group = new GroupLayout(DirectoryPanel);
        DirectoryPanel.setLayout(Group);
        Group.setAutoCreateGaps(true);
        Group.setAutoCreateContainerGaps(true);
        Group.setVerticalGroup(
                Group.createParallelGroup().addComponent(OutputDirectoryButton).addComponent(OutputDirectory)
        );
        Group.setHorizontalGroup(
                Group.createSequentialGroup().addComponent(OutputDirectoryButton).addComponent(OutputDirectory)
        );
        Group.linkSize(SwingUtilities.VERTICAL, OutputDirectoryButton, OutputDirectory);
    }
    private void initCenterPanel()
    {
        GroupLayout Group = new GroupLayout(CenterPanel);
        CenterPanel.setLayout(Group);
        Group.setAutoCreateGaps(true);
        Group.setAutoCreateContainerGaps(true);
        Group.setVerticalGroup(
                Group.createSequentialGroup().addComponent(DirectoryPanel)
        );
        Group.setHorizontalGroup(
                Group.createParallelGroup().addComponent(DirectoryPanel)
        );
    }
    private void eventDispatcher()
    {
        OutputDirectoryButton.addActionListener(directoryController);
        FullscreenButton.addActionListener(fullscreenController);
        SnippetButton.addActionListener(snippetController);
    }
    public void setOutputDirectory(String Path) {
        OutputDirectory.setText(Path);
    }
    public String getOutputDirectory()
    {
        return OutputDirectory.getText();
    }

}

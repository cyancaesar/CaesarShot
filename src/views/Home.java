package views;

import controllers.DirectoryController;
import controllers.FullscreenController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Home {

    public JFrame Frame;
    private JPanel MainPanel;
    private JPanel CenterPanel;
    private JPanel DirectoryPanel;

    private JButton FullscreenButton;
    private JButton ScreenshotButton;
    private JButton OutputDirectoryButton;

    private JTextField OutputDirectory;

    public Home()
    {
        this.createAndShowGui();
    }
    private void createAndShowGui()
    {
        Frame = new JFrame("CaesarShot");
        MainPanel = new JPanel(new BorderLayout(5, 10));
        DirectoryPanel = new JPanel();
        DirectoryPanel.setBorder(BorderFactory.createTitledBorder("Output Directory"));

        CenterPanel = new JPanel();

        FullscreenButton = createButton("Take Fullscreen Shot");
        ScreenshotButton = createButton("Take Screenshot");
        OutputDirectoryButton = createButton("Output");


        OutputDirectory = new JTextField("...", 20);
        OutputDirectory.setEditable(false);

        MainPanel.add(FullscreenButton, BorderLayout.NORTH);
        MainPanel.add(ScreenshotButton, BorderLayout.SOUTH);
        MainPanel.add(CenterPanel, BorderLayout.CENTER);
        MainPanel.setBorder(new EmptyBorder(20,30,20,30));

        this.eventsAttacher();
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
    private void eventsAttacher()
    {
        OutputDirectoryButton.addActionListener(new DirectoryController(this));
        FullscreenButton.addActionListener(new FullscreenController(this));
    }

    public void setOutputDirectory(String Path) {
        OutputDirectory.setText(Path);
    }

    public String getOutputDirectory()
    {
        return OutputDirectory.getText();
    }

    public void hideFrame()
    {
        Frame.setVisible(false);
    }

    public void showFrame()
    {
        Frame.setVisible(true);
    }

}

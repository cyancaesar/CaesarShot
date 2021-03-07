package views;

import controllers.DirectoryController;
import controllers.FullscreenController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Home {
    public final static Integer BASE_COLOR = 3028032;
    public final static Integer MAIN_COLOR = 3883602;
    public final static Integer SECOND_COLOR = 4410462;
    public final static Integer THIRD_COLOR = 5002858;

    public JFrame Frame;
    private JPanel MainPanel;
    private JPanel SidePanel;

    private JButton TakeFullscreenButton;
    private JButton TakeScreenshotButton;
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
        SidePanel = new JPanel();
        SidePanel.setBorder(BorderFactory.createTitledBorder("Output Directory"));

        TakeFullscreenButton = createButton("Take Fullscreen Shot");
        TakeScreenshotButton = createButton("Take Screenshot");
        OutputDirectoryButton = createButton("Output");

        OutputDirectory = new JTextField("...", 20);
        OutputDirectory.setEditable(false);

        MainPanel.add(TakeFullscreenButton, BorderLayout.NORTH);
        MainPanel.add(TakeScreenshotButton, BorderLayout.SOUTH);
        MainPanel.add(SidePanel, BorderLayout.CENTER);
        MainPanel.setBorder(new EmptyBorder(20,30,20,30));

        this.eventsAttacher();
        this.initSidePanel();
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
    private void initSidePanel()
    {
        GroupLayout Group = new GroupLayout(SidePanel);
        SidePanel.setLayout(Group);
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
    private void eventsAttacher()
    {
        OutputDirectoryButton.addActionListener(new DirectoryController(this));
        TakeFullscreenButton.addActionListener(new FullscreenController(this));
    }

    public void setOutputDirectory(String Path) {
        OutputDirectory.setText(Path);
    }
    public String getOutputDirectoryPath() {
        return OutputDirectory.getText();
    }


}

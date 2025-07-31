import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameWindow extends JFrame {
    private GamePanel gamePanel;
    
    public GameWindow() {
        initializeWindow();
        setupComponents();
    }
    
    private void initializeWindow() {
        setTitle("Space Impact - Java Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Remove setResizable(false) to allow minimize/maximize
        setMinimumSize(new Dimension(1280, 720)); // Minimum size to ensure content fits
        
        // Center on 1920x1200 screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(1280, 720); // Default size
        setLocation(
            (screenSize.width - 1280) / 2,
            (screenSize.height - 720) / 2
        );
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
    
    private void setupComponents() {
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);
        
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        
        JMenuItem instructionsItem = new JMenuItem("Instructions");
        instructionsItem.addActionListener(this::showInstructions);
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(this::showAbout);
        
        gameMenu.add(instructionsItem);
        gameMenu.addSeparator();
        gameMenu.add(aboutItem);
        
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
        
        pack();
    }
    
    private void showInstructions(ActionEvent e) {
        String instructions = 
            "SPACE IMPACT - Instructions\n\n" +
            "Controls:\n" +
            "• WASD or Arrow Keys - Move spaceship\n" +
            "• SPACE - Shoot\n" +
            "• P - Pause/Resume game\n" +
            "• Q - Quit to menu\n\n" +
            "Menu:\n" +
            "• Start Game > Easy/Medium/Hard\n" +
            "• Challenge Mode\n" +
            "• Leaderboard\n" +
            "• Exit\n\n" +
            "Objective:\n" +
            "• Destroy enemies to earn points\n" +
            "• Avoid enemy bullets and obstacles\n" +
            "• Survive as long as possible!";
            
        JOptionPane.showMessageDialog(this, instructions, "Instructions", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAbout(ActionEvent e) {
        String about = 
            "SPACE IMPACT\n" +
            "Java Edition\n\n" +
            "A classic arcade-style space shooter game.\n" +
            "Converted from C to Java with Swing GUI.\n\n" +
            "Features:\n" +
            "• Classic gameplay\n" +
            "• High score tracking\n" +
            "• Challenge mode\n" +
            "• Smooth controls\n\n" +
            "Version 1.0";
            
        JOptionPane.showMessageDialog(this, about, "About Space Impact", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
}
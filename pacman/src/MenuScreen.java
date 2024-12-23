package pacman.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuScreen extends JFrame implements ActionListener {

    private JButton level1Button, level2Button, level3Button, exitButton;     
    private JLabel logoLabel;;

    public MenuScreen() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Pacman Menu");
        setSize(380, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        loadLogo();
        createMenu();
        setVisible(true);
    }

    private void loadLogo() {
        ImageIcon logo = new ImageIcon("/Users/giakhanh/Desktop/DSA/Project/pacman/pacman/images/logo.png");
            
        // Apply the resized icon to the label
        logoLabel = new JLabel(logo);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        // Add padding around the label, specifically at the top
        logoLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0)); // top, left, bottom, right

        // Add the logo label to the frame at the NORTH position
        getContentPane().add(logoLabel, BorderLayout.NORTH);
    }
    

    private void createMenu() {
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridwidth = GridBagConstraints.REMAINDER; // each component in its own row
        gbc.fill = GridBagConstraints.HORIZONTAL;     // make components horizontally fill the space
        gbc.insets = new Insets(5, 50, 10, 50); // top, left, bottom, right padding

        // Invisible spacer
        gbc.weighty = 1;  // Allocate all extra space here, pushing buttons to the bottom
        buttonPanel.add(Box.createGlue(), gbc);

        // Reset weighty for buttons to 0
        gbc.weighty = 0;
        
        level1Button = createStyledButton("Level 1");
        level2Button = createStyledButton("Level 2");
        level3Button = createStyledButton("Level 3");
        exitButton = createStyledButton("Exit");


        // Add the buttons with the GridBagConstraints specified
        buttonPanel.add(level1Button, gbc);
        buttonPanel.add(level2Button, gbc);
        buttonPanel.add(level3Button, gbc);
        buttonPanel.add(exitButton, gbc);

        // Center the buttonPanel within the JFrame
        getContentPane().add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(5, 181, 79));
        button.addActionListener(this);
        button.setPreferredSize(new Dimension(100, 30));
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Button pressed: " + e.getActionCommand()); // Debugging statement
        if (e.getSource() == level1Button) {
            System.out.println("Opening Level 1");  // Debug statement
            openGameLevel(new Level1());
        } else if (e.getSource() == level2Button) {
            System.out.println("Opening Level 2");  // Debug statement
            openGameLevel(new Level2());
        } else if (e.getSource() == level3Button) {
            System.out.println("Opening Level 3");  // Debug statement
            openGameLevel(new Level3());
        } else if (e.getSource() == exitButton) {
            System.out.println("Exiting");  // Debug statement
            System.exit(0);
        }
    }

    private void openGameLevel(JPanel level) {
        JFrame gameFrame = new JFrame("Pacman");
        gameFrame.add(level);
        gameFrame.setSize(380, 420);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
        this.dispose();  // Close the menu screen after opening a level
    }
}

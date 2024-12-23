package pacman.src;

import javax.swing.*;
import java.awt.*;

public class Pacman extends JFrame {

    public Pacman(JPanel level) {
        add(level); // Add the level that is passed to the constructor
        setTitle("Pacman");
        setSize(380, 420);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(MenuScreen::new);
    }
}


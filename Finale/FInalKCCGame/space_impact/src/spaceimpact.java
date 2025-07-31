

import javax.swing.SwingUtilities;

public class spaceimpact {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameWindow().setVisible(true);
        });
    }
}
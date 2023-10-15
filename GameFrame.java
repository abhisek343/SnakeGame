import javax.swing.*;

public class GameFrame extends JFrame {
    GameFrame() {
        add(new GamePanel());
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}


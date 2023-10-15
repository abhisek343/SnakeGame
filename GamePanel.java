import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 1300;
    static final int SCREEN_HEIGHT = 750;
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 175;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int superApplesEaten;
    int appleX;
    int appleY;
    int superAppleX;
    int superAppleY;
    char direction = 'R';
    boolean running = false;
    boolean superAppleActive = false;
    Timer timer;
    Random random;
    String playerName = "Player"; // Default player name
    

    GamePanel() {
        random = new Random();
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.darkGray);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());

        // Get the player name
        playerName = JOptionPane.showInputDialog("Enter your name: ");

        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // Draw grid lines
            g.setColor(Color.black);
            for (int i = 0; i < SCREEN_WIDTH / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
            }
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            // Draw apples
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw super apple (if active)
            if (superAppleActive) {
                g.setColor(Color.orange);
                g.fillOval(superAppleX, superAppleY, UNIT_SIZE, UNIT_SIZE);
            }

            // Draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    // Draw the snake's head with eyes
                    g.setColor(Color.green);
                    g.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 15, 15);
                    g.setColor(Color.black);
                    g.fillOval(x[i] + UNIT_SIZE / 3, y[i] + UNIT_SIZE / 4, UNIT_SIZE / 6, UNIT_SIZE / 6);
                    g.fillOval(x[i] + UNIT_SIZE / 2, y[i] + UNIT_SIZE / 4, UNIT_SIZE / 6, UNIT_SIZE / 6);
                } else {
                    // Draw the snake's body segments
                    g.setColor(new Color(45, 180, 0));
                    g.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 15, 15);
                }
            }
    

            // Draw player name and score
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            g.drawString("Player: " + playerName, 10, 40);
            g.drawString("Score: " + applesEaten, 10, 80);
            g.drawString("Super Apples: " + superApplesEaten, 10, 120);
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    
        if (superAppleActive) {
            // Reset the super apple's position
            superAppleX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            superAppleY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        } else {
            // Create a new super apple
            superAppleActive = true;
    
            // Set the lifespan of the super apple
            int superAppleLifespan = DELAY * 100; // You can adjust the lifespan as needed
    
            // Start a timer to make the super apple disappear after its lifespan
            Timer superAppleTimer = new Timer(superAppleLifespan, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    superAppleActive = false;
                }
            });
            superAppleTimer.setRepeats(false);
            superAppleTimer.start();
        }
    }
    

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten += 1; // Boost the score by 5
            newApple();
        }
    }

    public void checkSuperApple() {
        if (superAppleActive && x[0] == superAppleX && y[0] == superAppleY) {
            superApplesEaten++;
            applesEaten += 2; // Boost the score by 10
            superAppleActive = false;
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkSuperApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (                direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                }
            }
        }
    }
    

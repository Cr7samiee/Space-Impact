import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*; // Covers other java.util classes
import java.util.List; 

public class GamePanel extends JPanel implements KeyListener, MouseListener, ActionListener {
    private static final int SCREEN_WIDTH = 1280;
    private static final int SCREEN_HEIGHT = 720;
    private static final int GAME_SPEED = 20;

    private GameEngine gameEngine;
    private javax.swing.Timer gameTimer;
    private Set<Integer> pressedKeys;
    private String playerName = "";
    private Rectangle[] homeMenuButtons;
    private Rectangle[] startGameMenuButtons;
    private Rectangle[] leaderboardMenuButtons;
    private Rectangle pauseIcon;
    private Rectangle[] pauseMenuButtons;
    private Rectangle[] gameOverButtons;
    private Rectangle nameEntryBackButton;
    private Rectangle leaderboardBackButton;
    private int highlightedButton = -1;
    private String selectedLeaderboardMode = "";

    public GamePanel() {
        gameEngine = new GameEngine();
        pressedKeys = new HashSet<>();
        homeMenuButtons = new Rectangle[4];
        startGameMenuButtons = new Rectangle[4];
        leaderboardMenuButtons = new Rectangle[4];
        pauseMenuButtons = new Rectangle[2];
        gameOverButtons = new Rectangle[2];
        pauseIcon = new Rectangle(SCREEN_WIDTH - 50, 10, 40, 40);
        nameEntryBackButton = new Rectangle();
        leaderboardBackButton = new Rectangle();

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHighlightedButton(e.getPoint());
                repaint();
            }
        });

        gameTimer = new javax.swing.Timer(GAME_SPEED, this);
        gameEngine.setGameState(GameState.HOME_MENU);
        requestFocusInWindow();
        gameTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Center the rendering within the panel
        int offsetX = (getWidth() - SCREEN_WIDTH) / 2;
        int offsetY = (getHeight() - SCREEN_HEIGHT) / 2;
        g2d.translate(offsetX, offsetY);

        switch (gameEngine.getGameState()) {
            case HOME_MENU:
                renderHomeMenu(g2d);
                break;
            case START_GAME_MENU:
                renderStartGameMenu(g2d);
                break;
            case NAME_ENTRY:
                renderNameEntry(g2d);
                break;
            case PLAYING:
                renderGame(g2d);
                break;
            case PAUSE_MENU:
                renderPauseMenu(g2d);
                break;
            case GAME_OVER:
                renderGameOver(g2d);
                break;
            case LEADERBOARD:
                renderLeaderboard(g2d);
                break;
            case LEADERBOARD_MENU:
                renderLeaderboardMenu(g2d);
                break;
        }
    }

    private void renderHomeMenu(Graphics2D g) {
        drawStarField(g);
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 64));
        FontMetrics fm = g.getFontMetrics();
        String title = "SPACE IMPACT";
        int titleX = (SCREEN_WIDTH - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, SCREEN_HEIGHT/2 - 180);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.ITALIC, 24));
        fm = g.getFontMetrics();
        String subtitle = "Java Edition";
        int subtitleX = (SCREEN_WIDTH - fm.stringWidth(subtitle)) / 2;
        g.drawString(subtitle, subtitleX, SCREEN_HEIGHT/2 - 120);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        fm = g.getFontMetrics();

        String[] menuItems = {"Start Game", "Challenge Mode", "Leaderboard", "Exit"};
        for (int i = 0; i < menuItems.length; i++) {
            int itemX = (SCREEN_WIDTH - fm.stringWidth(menuItems[i])) / 2;
            int itemY = SCREEN_HEIGHT/2 - 30 + (i * 60);
            if (i == highlightedButton && gameEngine.getGameState() == GameState.HOME_MENU) {
                g.setColor(new Color(0, 255, 255, 100));
                g.fillRect(itemX - 10, itemY - fm.getAscent(), fm.stringWidth(menuItems[i]) + 20, fm.getHeight() + 10);
            }
            g.setColor(Color.WHITE);
            g.drawString(menuItems[i], itemX, itemY);
            homeMenuButtons[i] = new Rectangle(itemX - 10, itemY - fm.getAscent(), fm.stringWidth(menuItems[i]) + 20, fm.getHeight() + 10);
        }

        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        fm = g.getFontMetrics();
        String instruction = "Click to select";
        int instX = (SCREEN_WIDTH - fm.stringWidth(instruction)) / 2;
        g.drawString(instruction, instX, SCREEN_HEIGHT - 60);

        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        fm = g.getFontMetrics();
        String highScore = "Current High Score: " + gameEngine.getHighestScore(gameEngine.isChallengeMode() ? "Challenge" : gameEngine.getDifficulty().toString());
        int hsX = (SCREEN_WIDTH - fm.stringWidth(highScore)) / 2;
        g.drawString(highScore, hsX, SCREEN_HEIGHT - 120);
    }

    private void renderStartGameMenu(Graphics2D g) {
        drawStarField(g);
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 64));
        FontMetrics fm = g.getFontMetrics();
        String title = "SELECT DIFFICULTY";
        int titleX = (SCREEN_WIDTH - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, SCREEN_HEIGHT/2 - 180);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        fm = g.getFontMetrics();

        String[] menuItems = {"Easy", "Medium", "Hard", "Back"};
        for (int i = 0; i < menuItems.length; i++) {
            int itemX = (SCREEN_WIDTH - fm.stringWidth(menuItems[i])) / 2;
            int itemY = SCREEN_HEIGHT/2 - 30 + (i * 60);
            if (i == highlightedButton && gameEngine.getGameState() == GameState.START_GAME_MENU) {
                g.setColor(new Color(0, 255, 255, 100));
                g.fillRect(itemX - 10, itemY - fm.getAscent(), fm.stringWidth(menuItems[i]) + 20, fm.getHeight() + 10);
            }
            g.setColor(Color.WHITE);
            g.drawString(menuItems[i], itemX, itemY);
            startGameMenuButtons[i] = new Rectangle(itemX - 10, itemY - fm.getAscent(), fm.stringWidth(menuItems[i]) + 20, fm.getHeight() + 10);
        }

        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        fm = g.getFontMetrics();
        String instruction = "Click to select";
        int instX = (SCREEN_WIDTH - fm.stringWidth(instruction)) / 2;
        g.drawString(instruction, instX, SCREEN_HEIGHT - 60);
    }

    private void renderLeaderboardMenu(Graphics2D g) {
        drawStarField(g);
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 64));
        FontMetrics fm = g.getFontMetrics();
        String title = "LEADERBOARD";
        int titleX = (SCREEN_WIDTH - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, SCREEN_HEIGHT/2 - 180);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        fm = g.getFontMetrics();

        String[] menuItems = {"Easy", "Medium", "Hard", "Challenge Mode"};
        for (int i = 0; i < menuItems.length; i++) {
            int itemX = (SCREEN_WIDTH - fm.stringWidth(menuItems[i])) / 2;
            int itemY = SCREEN_HEIGHT/2 - 30 + (i * 60);
            if (i == highlightedButton && gameEngine.getGameState() == GameState.LEADERBOARD_MENU) {
                g.setColor(new Color(0, 255, 255, 100));
                g.fillRect(itemX - 10, itemY - fm.getAscent(), fm.stringWidth(menuItems[i]) + 20, fm.getHeight() + 10);
            }
            g.setColor(Color.WHITE);
            g.drawString(menuItems[i], itemX, itemY);
            leaderboardMenuButtons[i] = new Rectangle(itemX - 10, itemY - fm.getAscent(), fm.stringWidth(menuItems[i]) + 20, fm.getHeight() + 10);
        }

        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        fm = g.getFontMetrics();
        String instruction = "Click to select";
        int instX = (SCREEN_WIDTH - fm.stringWidth(instruction)) / 2;
        g.drawString(instruction, instX, SCREEN_HEIGHT - 60);
    }

    private void renderNameEntry(Graphics2D g) {
        drawStarField(g);
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String title = "Enter Your Name";
        int titleX = (SCREEN_WIDTH - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, SCREEN_HEIGHT/2 - 90);

        g.setColor(Color.WHITE);
        g.drawRect(SCREEN_WIDTH/2 - 200, SCREEN_HEIGHT/2 - 30, 400, 60);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(SCREEN_WIDTH/2 - 198, SCREEN_HEIGHT/2 - 28, 396, 56);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        String displayName = playerName.isEmpty() ? "_" : playerName + (System.currentTimeMillis() % 1000 < 500 ? "_" : "");
        fm = g.getFontMetrics();
        int nameX = SCREEN_WIDTH/2 - 190;
        g.drawString(displayName, nameX, SCREEN_HEIGHT/2 + 10);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        String backText = "Back";
        int backX = (SCREEN_WIDTH - fm.stringWidth(backText)) / 2;
        int backY = SCREEN_HEIGHT/2 + 180;
        if (nameEntryBackButton.contains(getMousePosition() != null ? getMousePosition() : new Point())) {
            g.setColor(new Color(0, 255, 255, 100));
            g.fillRect(backX - 10, backY - fm.getAscent(), fm.stringWidth(backText) + 20, fm.getHeight() + 10);
        }
        g.setColor(Color.WHITE);
        g.drawString(backText, backX, backY);
        nameEntryBackButton.setBounds(backX - 10, backY - fm.getAscent(), fm.stringWidth(backText) + 20, fm.getHeight() + 10);

        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        fm = g.getFontMetrics();
        String[] instructions = {
            "Type your name (max 15 characters)",
            "Press Enter to start, Backspace to delete",
            "Letters, digits, and spaces only"
        };
        for (int i = 0; i < instructions.length; i++) {
            int instX = (SCREEN_WIDTH - fm.stringWidth(instructions[i])) / 2;
            g.drawString(instructions[i], instX, SCREEN_HEIGHT/2 + 90 + (i * 30));
        }
    }

    private void renderGame(Graphics2D g) {
        drawStarField(g);
        if (gameEngine.getSpaceship() != null) {
            gameEngine.getSpaceship().render(g);
        }
        for (Enemy enemy : gameEngine.getEnemies()) {
            enemy.render(g);
        }
        for (Bullet bullet : gameEngine.getBullets()) {
            bullet.render(g);
        }
        for (Obstacles obstacle : gameEngine.getObstacles()) {
            obstacle.render(g);
        }
        for (Wall wall : gameEngine.getWalls()) {
            wall.render(g);
        }
        for (PowerUp powerUp : gameEngine.getPowerUps()) {
            powerUp.render(g);
        }
        if (gameEngine.getUFO() != null) {
            gameEngine.getUFO().render(g);
        }
        renderGameUI(g);
        g.setColor(Color.YELLOW);
        g.fillRect(pauseIcon.x, pauseIcon.y, pauseIcon.width, pauseIcon.height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("||", pauseIcon.x + 12, pauseIcon.y + 28);
    }

    private void renderGameUI(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, SCREEN_WIDTH, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + gameEngine.getScore(), 20, 35);
        g.setColor(Color.RED);
        g.drawString("Lives: " + gameEngine.getLives(), SCREEN_WIDTH - 150, 35);
        g.setColor(Color.CYAN);
        String difficulty = "Mode: " + (gameEngine.isChallengeMode() ? "Challenge" : gameEngine.getDifficulty().toString());
        g.drawString(difficulty, SCREEN_WIDTH/2 - 60, 35);
        if (gameEngine.getUFO() != null) {
            g.setColor(Color.YELLOW);
            g.drawString("UFO HP: " + gameEngine.getUFO().getHealth(), SCREEN_WIDTH/2 + 100, 35);
        }
        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("P-Pause | Q-Quit | Space-Shoot", 20, SCREEN_HEIGHT - 15);
    }

    private void renderPauseMenu(Graphics2D g) {
        renderGame(g);
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 64));
        FontMetrics fm = g.getFontMetrics();
        String pauseText = "PAUSED";
        int textX = (SCREEN_WIDTH - fm.stringWidth(pauseText)) / 2;
        g.drawString(pauseText, textX, SCREEN_HEIGHT/2 - 120);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        fm = g.getFontMetrics();

        String[] pauseOptions = {"Continue Game", "Exit"};
        for (int i = 0; i < pauseOptions.length; i++) {
            int itemX = (SCREEN_WIDTH - fm.stringWidth(pauseOptions[i])) / 2;
            int itemY = SCREEN_HEIGHT/2 - 30 + (i * 60);
            if (i == highlightedButton && gameEngine.getGameState() == GameState.PAUSE_MENU) {
                g.setColor(new Color(0, 255, 255, 100));
                g.fillRect(itemX - 10, itemY - fm.getAscent(), fm.stringWidth(pauseOptions[i]) + 20, fm.getHeight() + 10);
            }
            g.setColor(Color.WHITE);
            g.drawString(pauseOptions[i], itemX, itemY);
            pauseMenuButtons[i] = new Rectangle(itemX - 10, itemY - fm.getAscent(), fm.stringWidth(pauseOptions[i]) + 20, fm.getHeight() + 10);
        }

        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        fm = g.getFontMetrics();
        String instruction = "Click to select";
        int instX = (SCREEN_WIDTH - fm.stringWidth(instruction)) / 2;
        g.drawString(instruction, instX, SCREEN_HEIGHT - 60);
    }

    private void renderGameOver(Graphics2D g) {
        drawStarField(g);
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 72));
        FontMetrics fm = g.getFontMetrics();
        String gameOverText = "GAME OVER";
        int goX = (SCREEN_WIDTH - fm.stringWidth(gameOverText)) / 2;
        g.drawString(gameOverText, goX, SCREEN_HEIGHT/2 - 120);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        fm = g.getFontMetrics();
        String scoreText = "Final Score: " + gameEngine.getScore();
        int scoreX = (SCREEN_WIDTH - fm.stringWidth(scoreText)) / 2;
        g.drawString(scoreText, scoreX, SCREEN_HEIGHT/2 - 30);

        if (gameEngine.getScore() > 0 && gameEngine.getScore() >= gameEngine.getHighestScore(gameEngine.isChallengeMode() ? "Challenge" : gameEngine.getDifficulty().toString())) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            fm = g.getFontMetrics();
            String newRecord = "NEW HIGH SCORE!";
            int recordX = (SCREEN_WIDTH - fm.stringWidth(newRecord)) / 2;
            g.drawString(newRecord, recordX, SCREEN_HEIGHT/2 + 20);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        fm = g.getFontMetrics();

        String[] options = {"Play Again", "Return Home"};
        for (int i = 0; i < options.length; i++) {
            int optX = (SCREEN_WIDTH - fm.stringWidth(options[i])) / 2;
            int optY = SCREEN_HEIGHT/2 + 80 + (i * 60);
            if (i == highlightedButton && gameEngine.getGameState() == GameState.GAME_OVER) {
                g.setColor(new Color(0, 255, 255, 100));
                g.fillRect(optX - 10, optY - fm.getAscent(), fm.stringWidth(options[i]) + 20, fm.getHeight() + 10);
            }
            g.setColor(Color.WHITE);
            g.drawString(options[i], optX, optY);
            gameOverButtons[i] = new Rectangle(optX - 10, optY - fm.getAscent(), fm.stringWidth(options[i]) + 20, fm.getHeight() + 10);
        }

        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        fm = g.getFontMetrics();
        String instruction = "Click to select";
        int instX = (SCREEN_WIDTH - fm.stringWidth(instruction)) / 2;
        g.drawString(instruction, instX, SCREEN_HEIGHT - 60);
    }

    private void renderLeaderboard(Graphics2D g) {
        drawStarField(g);
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String title = selectedLeaderboardMode.toUpperCase() + " HIGH SCORES";
        int titleX = (SCREEN_WIDTH - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, SCREEN_HEIGHT/2 - 180);

        List<LeaderboardEntry> leaderboard = gameEngine.getLeaderboardManager().getLeaderboard(selectedLeaderboardMode);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        for (int i = 0; i < Math.min(leaderboard.size(), 10); i++) {
            LeaderboardEntry entry = leaderboard.get(i);
            Color rankColor = switch (i) {
                case 0 -> Color.RED;
                case 1 -> new Color(192, 192, 192);
                case 2 -> new Color(205, 127, 50);
                default -> Color.WHITE;
            };
            g.setColor(rankColor);
            String rankText = (i + 1) + ".";
            g.drawString(rankText, SCREEN_WIDTH/2 - 50, SCREEN_HEIGHT/2 - 90 + (i * 40));
            g.drawString(entry.getName(), SCREEN_WIDTH/2 - 10, SCREEN_HEIGHT/2 - 90 + (i * 40));
            String scoreText = String.valueOf(entry.getScore());
            fm = g.getFontMetrics();
            int scoreX = SCREEN_WIDTH/2 + 150 - fm.stringWidth(scoreText);
            g.drawString(scoreText, scoreX, SCREEN_HEIGHT/2 - 90 + (i * 40));
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        String backText = "Back";
        int backX = (SCREEN_WIDTH - fm.stringWidth(backText)) / 2;
        int backY = SCREEN_HEIGHT - 100;
        if (leaderboardBackButton.contains(getMousePosition() != null ? getMousePosition() : new Point())) {
            g.setColor(new Color(0, 255, 255, 100));
            g.fillRect(backX - 10, backY - fm.getAscent(), fm.stringWidth(backText) + 20, fm.getHeight() + 10);
        }
        g.setColor(Color.WHITE);
        g.drawString(backText, backX, backY);
        leaderboardBackButton.setBounds(backX - 10, backY - fm.getAscent(), fm.stringWidth(backText) + 20, fm.getHeight() + 10);

        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        fm = g.getFontMetrics();
        String returnText = "Click Back or press any key to return";
        int instX = (SCREEN_WIDTH - fm.stringWidth(returnText)) / 2;
        g.drawString(returnText, instX, SCREEN_HEIGHT - 50);
    }

    private void drawStarField(Graphics2D g) {
        g.setColor(Color.WHITE);
        int offset = (int)(System.currentTimeMillis() / 50 % SCREEN_HEIGHT);
        for (int i = 0; i < 100; i++) {
            int x = (i * 37) % SCREEN_WIDTH;
            int y = ((i * 23) + offset) % SCREEN_HEIGHT;
            int size = (i % 3) + 1;
            g.fillOval(x, y, size, size);
        }
    }

    private void updateHighlightedButton(Point p) {
        highlightedButton = -1;
        if (p == null) return;
        int offsetX = (getWidth() - SCREEN_WIDTH) / 2;
        int offsetY = (getHeight() - SCREEN_HEIGHT) / 2;
        Point adjustedPoint = new Point(p.x - offsetX, p.y - offsetY);
        switch (gameEngine.getGameState()) {
            case HOME_MENU:
                for (int i = 0; i < homeMenuButtons.length; i++) {
                    if (homeMenuButtons[i] != null && homeMenuButtons[i].contains(adjustedPoint)) {
                        highlightedButton = i;
                        break;
                    }
                }
                break;
            case START_GAME_MENU:
                for (int i = 0; i < startGameMenuButtons.length; i++) {
                    if (startGameMenuButtons[i] != null && startGameMenuButtons[i].contains(adjustedPoint)) {
                        highlightedButton = i;
                        break;
                    }
                }
                break;
            case LEADERBOARD_MENU:
                for (int i = 0; i < leaderboardMenuButtons.length; i++) {
                    if (leaderboardMenuButtons[i] != null && leaderboardMenuButtons[i].contains(adjustedPoint)) {
                        highlightedButton = i;
                        break;
                    }
                }
                break;
            case PAUSE_MENU:
                for (int i = 0; i < pauseMenuButtons.length; i++) {
                    if (pauseMenuButtons[i] != null && pauseMenuButtons[i].contains(adjustedPoint)) {
                        highlightedButton = i;
                        break;
                    }
                }
                break;
            case GAME_OVER:
                for (int i = 0; i < gameOverButtons.length; i++) {
                    if (gameOverButtons[i] != null && gameOverButtons[i].contains(adjustedPoint)) {
                        highlightedButton = i;
                        break;
                    }
                }
                break;
            case NAME_ENTRY:
                if (nameEntryBackButton.contains(adjustedPoint)) {
                    highlightedButton = -1;
                }
                break;
            case LEADERBOARD:
                if (leaderboardBackButton.contains(adjustedPoint)) {
                    highlightedButton = -1;
                }
                break;
            default:
                break;
        }
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameEngine.getGameState() == GameState.PLAYING) {
            if (pressedKeys.contains(KeyEvent.VK_A) || pressedKeys.contains(KeyEvent.VK_LEFT)) {
                gameEngine.moveSpaceshipLeft();
            }
            if (pressedKeys.contains(KeyEvent.VK_D) || pressedKeys.contains(KeyEvent.VK_RIGHT)) {
                gameEngine.moveRight();
            }
            if (pressedKeys.contains(KeyEvent.VK_W) || pressedKeys.contains(KeyEvent.VK_UP)) {
                gameEngine.moveUp();
            }
            if (pressedKeys.contains(KeyEvent.VK_S) || pressedKeys.contains(KeyEvent.VK_DOWN)) {
                gameEngine.moveDown();
            }
            if (pressedKeys.contains(KeyEvent.VK_SPACE)) {
                gameEngine.shoot();
            }
            gameEngine.update();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        pressedKeys.add(keyCode);
        switch (gameEngine.getGameState()) {
            case PLAYING:
                handleGameInput(keyCode);
                break;
            case PAUSE_MENU:
                if (keyCode == KeyEvent.VK_P) {
                    gameEngine.setGameState(GameState.PLAYING);
                    gameTimer.start();
                }
                break;
            case GAME_OVER:
                break;
            case LEADERBOARD:
                gameEngine.setGameState(GameState.LEADERBOARD_MENU);
                selectedLeaderboardMode = "";
                repaint();
                break;
            case LEADERBOARD_MENU:
                gameEngine.setGameState(GameState.HOME_MENU);
                selectedLeaderboardMode = "";
                repaint();
                break;
            case NAME_ENTRY:
                handleNameEntryInput(keyCode, e.getKeyChar());
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    private void handleGameInput(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_P:
                gameEngine.setGameState(GameState.PAUSE_MENU);
                gameTimer.stop();
                highlightedButton = -1;
                repaint();
                break;
            case KeyEvent.VK_Q:
                System.out.println("Quitting game via Q key, saving score: " + gameEngine.getScore());
                gameEngine.addScoreToLeaderboard(playerName.trim(), gameEngine.isChallengeMode() ? "Challenge" : gameEngine.getDifficulty().toString());
                gameEngine.resetGame();
                gameEngine.setGameState(GameState.HOME_MENU);
                highlightedButton = -1;
                repaint();
                break;
        }
    }

    private void handleNameEntryInput(int keyCode, char keyChar) {
        if (keyCode == KeyEvent.VK_ENTER) {
            if (!playerName.trim().isEmpty()) {
                gameEngine.startGame();
                gameEngine.setGameState(GameState.PLAYING);
                gameTimer.start();
                highlightedButton = -1;
                repaint();
            } else {
                System.out.println("Cannot start game: Player name is empty");
            }
        } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            if (!playerName.isEmpty()) {
                playerName = playerName.substring(0, playerName.length() - 1);
            }
            repaint();
        } else if (Character.isLetterOrDigit(keyChar) || keyChar == ' ') {
            if (playerName.length() < 15) {
                playerName += keyChar;
            }
            repaint();
        }
    }

    private void handleHomeMenuSelection(int option) {
        switch (option) {
            case 0:
                gameEngine.setGameState(GameState.START_GAME_MENU);
                highlightedButton = -1;
                repaint();
                break;
            case 1:
                gameEngine.setGameState(GameState.NAME_ENTRY);
                playerName = "";
                gameEngine.setChallengeMode(true);
                highlightedButton = -1;
                repaint();
                break;
            case 2:
                gameEngine.setGameState(GameState.LEADERBOARD_MENU);
                highlightedButton = -1;
                repaint();
                break;
            case 3:
                System.exit(0);
                break;
        }
    }

    private void handleStartGameMenuSelection(int option) {
        switch (option) {
            case 0:
                gameEngine.setGameState(GameState.NAME_ENTRY);
                playerName = "";
                gameEngine.setChallengeMode(false);
                gameEngine.setPendingDifficulty(Difficulty.EASY);
                highlightedButton = -1;
                repaint();
                break;
            case 1:
                gameEngine.setGameState(GameState.NAME_ENTRY);
                playerName = "";
                gameEngine.setChallengeMode(false);
                gameEngine.setPendingDifficulty(Difficulty.MEDIUM);
                highlightedButton = -1;
                repaint();
                break;
            case 2:
                gameEngine.setGameState(GameState.NAME_ENTRY);
                playerName = "";
                gameEngine.setChallengeMode(false);
                gameEngine.setPendingDifficulty(Difficulty.HARD);
                highlightedButton = -1;
                repaint();
                break;
            case 3:
                gameEngine.setGameState(GameState.HOME_MENU);
                highlightedButton = -1;
                repaint();
                break;
        }
    }

    private void handleLeaderboardMenuSelection(int option) {
        switch (option) {
            case 0:
                selectedLeaderboardMode = "Easy";
                gameEngine.setGameState(GameState.LEADERBOARD);
                highlightedButton = -1;
                repaint();
                break;
            case 1:
                selectedLeaderboardMode = "Medium";
                gameEngine.setGameState(GameState.LEADERBOARD);
                highlightedButton = -1;
                repaint();
                break;
            case 2:
                selectedLeaderboardMode = "Hard";
                gameEngine.setGameState(GameState.LEADERBOARD);
                highlightedButton = -1;
                repaint();
                break;
            case 3:
                selectedLeaderboardMode = "Challenge";
                gameEngine.setGameState(GameState.LEADERBOARD);
                highlightedButton = -1;
                repaint();
                break;
        }
    }

    private void handlePauseMenuSelection(int option) {
        switch (option) {
            case 0:
                gameEngine.setGameState(GameState.PLAYING);
                gameTimer.start();
                highlightedButton = -1;
                repaint();
                break;
            case 1:
                System.out.println("Exiting from pause menu, saving score: " + gameEngine.getScore());
                gameEngine.addScoreToLeaderboard(playerName.trim(), gameEngine.isChallengeMode() ? "Challenge" : gameEngine.getDifficulty().toString());
                gameEngine.resetGame();
                gameEngine.setGameState(GameState.HOME_MENU);
                highlightedButton = -1;
                repaint();
                break;
        }
    }

    private void handleGameOverSelection(int option) {
        switch (option) {
            case 0:
                System.out.println("Play Again selected, saving score: " + gameEngine.getScore());
                gameEngine.addScoreToLeaderboard(playerName.trim(), gameEngine.isChallengeMode() ? "Challenge" : gameEngine.getDifficulty().toString());
                gameEngine.resetGame();
                gameEngine.setGameState(GameState.START_GAME_MENU);
                highlightedButton = -1;
                repaint();
                break;
            case 1:
                System.out.println("Return Home selected, saving score: " + gameEngine.getScore());
                gameEngine.addScoreToLeaderboard(playerName.trim(), gameEngine.isChallengeMode() ? "Challenge" : gameEngine.getDifficulty().toString());
                gameEngine.resetGame();
                gameEngine.setGameState(GameState.HOME_MENU);
                highlightedButton = -1;
                repaint();
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int offsetX = (getWidth() - SCREEN_WIDTH) / 2;
        int offsetY = (getHeight() - SCREEN_HEIGHT) / 2;
        Point clickPoint = new Point(e.getX() - offsetX, e.getY() - offsetY);
        switch (gameEngine.getGameState()) {
            case HOME_MENU:
                for (int i = 0; i < homeMenuButtons.length; i++) {
                    if (homeMenuButtons[i] != null && homeMenuButtons[i].contains(clickPoint)) {
                        handleHomeMenuSelection(i);
                        break;
                    }
                }
                break;
            case START_GAME_MENU:
                for (int i = 0; i < startGameMenuButtons.length; i++) {
                    if (startGameMenuButtons[i] != null && startGameMenuButtons[i].contains(clickPoint)) {
                        handleStartGameMenuSelection(i);
                        break;
                    }
                }
                break;
            case LEADERBOARD_MENU:
                for (int i = 0; i < leaderboardMenuButtons.length; i++) {
                    if (leaderboardMenuButtons[i] != null && leaderboardMenuButtons[i].contains(clickPoint)) {
                        handleLeaderboardMenuSelection(i);
                        break;
                    }
                }
                break;
            case PLAYING:
                if (pauseIcon.contains(clickPoint)) {
                    gameEngine.setGameState(GameState.PAUSE_MENU);
                    gameTimer.stop();
                    highlightedButton = -1;
                    repaint();
                }
                break;
            case PAUSE_MENU:
                for (int i = 0; i < pauseMenuButtons.length; i++) {
                    if (pauseMenuButtons[i] != null && pauseMenuButtons[i].contains(clickPoint)) {
                        handlePauseMenuSelection(i);
                        break;
                    }
                }
                break;
            case GAME_OVER:
                for (int i = 0; i < gameOverButtons.length; i++) {
                    if (gameOverButtons[i] != null && gameOverButtons[i].contains(clickPoint)) {
                        handleGameOverSelection(i);
                        break;
                    }
                }
                break;
            case NAME_ENTRY:
                if (nameEntryBackButton.contains(clickPoint)) {
                    gameEngine.setGameState(GameState.HOME_MENU);
                    playerName = "";
                    highlightedButton = -1;
                    repaint();
                }
                break;
            case LEADERBOARD:
                if (leaderboardBackButton.contains(clickPoint)) {
                    gameEngine.setGameState(GameState.LEADERBOARD_MENU);
                    selectedLeaderboardMode = "";
                    highlightedButton = -1;
                    repaint();
                } else {
                    gameEngine.setGameState(GameState.LEADERBOARD_MENU);
                    selectedLeaderboardMode = "";
                    highlightedButton = -1;
                    repaint();
                }
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}
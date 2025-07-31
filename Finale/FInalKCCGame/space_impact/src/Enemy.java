import java.awt.*;
import java.util.List;
import java.util.Random;

public class Enemy extends GameObject {
    private int baseMoveSpeed;
    private int shootProbability;
    private Random random;
    private int screenWidth;
    private int screenHeight;
    private long lastFireTime = 0;
    private static final long FIRE_COOLDOWN = 1000; // 1 second cooldown
    private GameEngine gameEngine; // Reference to GameEngine instance

    public Enemy(int x, int y, int screenWidth, int screenHeight, int baseMoveSpeed, int shootProbability, GameEngine gameEngine) {
        super(x, y);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.baseMoveSpeed = baseMoveSpeed;
        this.shootProbability = shootProbability * 2;
        this.random = new Random();
        this.gameEngine = gameEngine; // Initialize with GameEngine instance
    }

    public void update(int score, List<Bullet> bullets) {
        int moveSpeed = baseMoveSpeed + (score / 100);
        y += moveSpeed;
        if (y >= screenHeight) {
            respawn();
        }
        if (shouldShoot()) {
            shoot(bullets); // Pass the bullets list
        }
    }

    @Override
    public void update() {
        update(0, null); // Default implementation, adjust as needed
    }

    @Override
    public void render(Graphics g) {
        if (active) {
            g.setColor(Color.RED);
            g.fillOval(x - 15, y - 15, 30, 30);
            g.setColor(Color.BLACK);
            g.fillOval(x - 7, y - 7, 14, 14);
        }
    }

    @Override
    protected Color getColor() {
        return Color.RED;
    }

    public boolean shouldShoot() {
        int adjustedProbability = shootProbability + (getDifficultyScoreMultiplier() * 2);
        return random.nextInt(100) < adjustedProbability;
    }

    public void shoot(List<Bullet> gameBullets) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFireTime >= FIRE_COOLDOWN && gameBullets != null) {
            for (int i = -1; i <= 1; i++) {
                gameBullets.add(new Bullet(x + (i * 5), y + 20, false, i * 2, 10));
            }
            lastFireTime = currentTime;
        }
    }

    private int getDifficultyScoreMultiplier() {
        Difficulty diff = gameEngine.getDifficulty(); // Call on instance
        return switch (diff) {
            case EASY -> 0;
            case MEDIUM -> 2;
            case HARD -> 5;
        };
    }

    public void respawn() {
        y = 30;
        x = random.nextInt(screenWidth - 60) + 30;
        active = true;
    }

    public void setBaseMoveSpeed(int speed) {
        this.baseMoveSpeed = speed;
    }

    public void setShootProbability(int prob) {
        this.shootProbability = prob * 2;
    }
}
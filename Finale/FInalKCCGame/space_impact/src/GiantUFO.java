import java.awt.*;
import java.util.Random;

public class GiantUFO extends GameObject {
    private int health;
    private int missileCount;
    private int screenWidth;
    private int screenHeight;
    private int baseMoveSpeed;
    private Random random;

    public GiantUFO(int x, int y, int screenWidth, int screenHeight, int baseMoveSpeed, int health) {
        super(x, y);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.baseMoveSpeed = baseMoveSpeed;
        this.health = health;
        this.missileCount = 0;
        this.random = new Random();
    }

    @Override
    public void update() {
        int moveSpeed = baseMoveSpeed;
        y += moveSpeed;
        if (y >= screenHeight) {
            y = 30;
            x = random.nextInt(screenWidth - 100) + 50;
        }
    }

    @Override
    public void render(Graphics g) {
        if (active) {
            g.setColor(Color.RED);
            g.fillRect(x - 50, y - 25, 100, 50);
            g.setColor(Color.YELLOW);
            g.fillOval(x - 20, y - 10, 40, 20);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("HP: " + health, x - 20, y - 30);
        }
    }

    @Override
    protected Color getColor() {
        return Color.RED;
    }

    public void hit() {
        missileCount++;
        health--;
        if (health <= 0) {
            active = false;
        }
    }

    public boolean shouldFire() {
        return missileCount >= 10;
    }

    public Bullet[] fire() {
        missileCount = 0;
        Bullet[] bullets = new Bullet[5];
        for (int i = 0; i < 5; i++) {
            int angle = -60 + (i * 30); // Fan pattern: -60, -30, 0, 30, 60 degrees
            int speedX = (int) (Math.sin(Math.toRadians(angle)) * 10);
            int speedY = (int) (Math.cos(Math.toRadians(angle)) * 10);
            bullets[i] = new Bullet(x, y + 30, false, speedX, speedY);
        }
        return bullets;
    }

    public int getHealth() {
        return health;
    }

    public void respawn(int newHealth) {
        y = 30;
        x = random.nextInt(screenWidth - 100) + 50;
        health = newHealth;
        missileCount = 0;
        active = true;
    }
}
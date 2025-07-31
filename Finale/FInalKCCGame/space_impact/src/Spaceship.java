import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Spaceship extends GameObject {
    private int baseMoveSpeed = 10;
    private boolean hasTripleShot;
    private long powerUpEndTime;
    private long lastFireTime = 0;
    private static final long FIRE_COOLDOWN = 300; // 300ms cooldown

    public Spaceship(int x, int y) {
        super(x, y);
    }

    @Override
    public void update() {
        if (System.currentTimeMillis() >= powerUpEndTime) {
            hasTripleShot = false;
        }
    }

    @Override
    public void render(Graphics g) {
        if (active) {
            g.setColor(Color.CYAN);
            int[] xPoints = {x, x - 15, x + 15};
            int[] yPoints = {y - 15, y + 15, y + 15};
            g.fillPolygon(xPoints, yPoints, 3);
        }
    }

    @Override
    protected Color getColor() {
        return Color.CYAN;
    }

    public void moveLeft(int screenWidth) {
        if (x > 15) {
            x -= baseMoveSpeed;
        }
    }

    public void moveRight(int screenWidth) {
        if (x < screenWidth - 15) {
            x += baseMoveSpeed;
        }
    }

    public void moveUp() {
        if (y > 15) {
            y -= baseMoveSpeed;
        }
    }

    public void moveDown(int screenHeight) {
        if (y < screenHeight - 15) {
            y += baseMoveSpeed;
        }
    }

    public List<Bullet> shoot() {
        List<Bullet> bullets = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFireTime >= FIRE_COOLDOWN) {
            if (hasTripleShot) {
                for (int i = -1; i <= 1; i++) {
                    bullets.add(new Bullet(x + (i * 10), y - 20, true, i * 2, -10));
                }
            } else {
                bullets.add(new Bullet(x, y - 20, true));
            }
            lastFireTime = currentTime;
        }
        return bullets;
    }

    public void applyPowerUp(PowerUp.Type type) {
        switch (type) {
            case TRIPLE_SHOT:
                hasTripleShot = true;
                powerUpEndTime = System.currentTimeMillis() + 10000;
                break;
            case EXTRA_LIFE:
                // Handled in GameEngine
                break;
        }
    }
}
import java.awt.*;

public class Bullet extends GameObject {
    private boolean isPlayerBullet;
    private int speedX;
    private int speedY;

    public Bullet(int x, int y, boolean isPlayerBullet) {
        this(x, y, isPlayerBullet, 0, isPlayerBullet ? -10 : 10);
    }

    public Bullet(int x, int y, boolean isPlayerBullet, int speedX, int speedY) {
        super(x, y);
        this.isPlayerBullet = isPlayerBullet;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    @Override
    public void update() {
        x += speedX;
        y += speedY;
        if (x < 0 || x > 1280 || y < 0 || y > 720) {
            active = false;
        }
    }

    @Override
    public void render(Graphics g) {
        if (active) {
            g.setColor(isPlayerBullet ? Color.WHITE : Color.YELLOW);
            g.fillRect(x - 2, y - 5, 4, 10);
        }
    }

    @Override
    protected Color getColor() {
        return isPlayerBullet ? Color.WHITE : Color.YELLOW;
    }

    public boolean isPlayerBullet() {
        return isPlayerBullet;
    }
}
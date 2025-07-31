import java.awt.*;

public class Wall extends GameObject {
    private int screenWidth;
    private int gapX;
    private int gapWidth;
    private int speed;

    public Wall(int x, int y, int screenWidth, int gapX, int gapWidth, int speed) {
        super(x, y);
        this.screenWidth = screenWidth;
        this.gapX = gapX;
        this.gapWidth = gapWidth;
        this.speed = speed;
    }

    @Override
    public void update() {
        y += speed;
        if (y > 720) {
            active = false;
        }
    }

    @Override
    public void render(Graphics g) {
        if (active) {
            g.setColor(Color.GRAY);
            g.fillRect(0, y, gapX, 30);
            g.fillRect(gapX + gapWidth, y, screenWidth - (gapX + gapWidth), 30);
        }
    }

    @Override
    protected Color getColor() {
        return Color.GRAY;
    }

    @Override
    public boolean isColliding(GameObject other) {
        if (!active || !other.isActive()) {
            return false;
        }
        Rectangle otherBounds = new Rectangle(other.getX() - 15, other.getY() - 15, 30, 30);
        Rectangle leftWall = new Rectangle(0, y, gapX, 30);
        Rectangle rightWall = new Rectangle(gapX + gapWidth, y, screenWidth - (gapX + gapWidth), 30);
        return leftWall.intersects(otherBounds) || rightWall.intersects(otherBounds);
    }

    public int getGapX() { return gapX; }
    public int getGapWidth() { return gapWidth; }
}
import java.awt.*;

public class PowerUp extends GameObject {
    public enum Type { EXTRA_LIFE, TRIPLE_SHOT }
    private Type type;
    private int speed = 3;

    public PowerUp(int x, int y, Type type) {
        super(x, y);
        this.type = type;
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
            switch (type) {
                case EXTRA_LIFE:
                    g.setColor(Color.GREEN);
                    g.fillRect(x - 15, y - 15, 30, 30);
                    g.setColor(Color.WHITE);
                    g.drawString("L", x - 5, y + 5);
                    break;
                case TRIPLE_SHOT:
                    g.setColor(Color.RED);
                    g.fillRect(x - 15, y - 15, 30, 30);
                    g.setColor(Color.WHITE);
                    g.drawString("T", x - 5, y + 5);
                    break;
            }
        }
    }

    @Override
    protected Color getColor() {
        return switch (type) {
            case EXTRA_LIFE -> Color.GREEN;
            case TRIPLE_SHOT -> Color.RED;
        };
    }

    public Type getType() {
        return type;
    }
}
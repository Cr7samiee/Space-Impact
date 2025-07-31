import java.awt.Color;
import java.awt.Graphics;

public class Obstacles extends GameObject {
    private final int baseMoveSpeed;
    
    public Obstacles(int x, int y, int baseMoveSpeed) {
        super(x, y);
        this.baseMoveSpeed = baseMoveSpeed;
    }
    
    public void update(int score) {
        int moveSpeed = baseMoveSpeed + (score / 100);
        y += moveSpeed;
        
        if (y >= 720) {
            active = false;
        }
    }
    
    @Override
    public void update() {
        update(0);
    }
    
    @Override
    public void render(Graphics g) {
        if (active) {
            g.setColor(Color.GRAY);
            g.fillRect(x - 15, y - 15, 30, 30);
            g.setColor(Color.BLACK);
            g.drawLine(x - 15, y - 15, x + 15, y + 15);
        }
    }
    
    @Override
    protected Color getColor() {
        return Color.GRAY;
    }
}
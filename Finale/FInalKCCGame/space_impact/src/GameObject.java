import java.awt.Graphics;
import java.awt.Color;

public abstract class GameObject {
    protected int x, y;
    protected boolean active;
    
    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
        this.active = true;
    }
    
    public abstract void update();
    public abstract void render(Graphics g);
    protected abstract Color getColor();
    
    public boolean isColliding(GameObject other) {
        if (!active || !other.active) return false;
        
        int dx = Math.abs(this.x - other.x);
        int dy = Math.abs(this.y - other.y);
        
        return dx <= 15 && dy <= 15;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
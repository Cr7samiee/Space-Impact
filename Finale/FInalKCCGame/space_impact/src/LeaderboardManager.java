
import java.util.*;

public class LeaderboardManager {
    private DatabaseManager dbManager;
    
    public LeaderboardManager() {
        dbManager = new DatabaseManager();
    }
    
    public void addScore(String name, int score, String mode) {
        dbManager.addScore(name, score, mode);
    }
    
    public List<LeaderboardEntry> getLeaderboard(String mode) {
        List<LeaderboardEntry> leaderboard = dbManager.getLeaderboard(mode);
        Collections.sort(leaderboard); // Ensure descending order
        return leaderboard;
    }
    
    public int getHighestScore(String mode) {
        return dbManager.getHighestScore(mode);
    }
}
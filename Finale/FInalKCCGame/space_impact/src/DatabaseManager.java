import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bhavi";
    private static final String USER = "root";
    private static final String PASS = "#Zayn#123";
    private static final int LEADERBOARD_SIZE = 5;

    public DatabaseManager() {
        initializeDatabase();
    }

    private Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Database connection established successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error connecting to MySQL database: " + e.getMessage());
        }
        return con;
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS leaderboard (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY, " +
                         "name VARCHAR(50) NOT NULL, " +
                         "score INT NOT NULL, " +
                         "difficulty VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', " +
                         "is_challenge_mode TINYINT(1) NOT NULL DEFAULT 0)";
            stmt.execute(sql);
            System.out.println("Leaderboard table initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public void addScore(String name, int score, String mode) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println("Cannot add score: Player name is empty or null");
            return;
        }
        if (score <= 0) {
            System.out.println("Score not added: Score is " + score);
            return;
        }
        String formattedMode = formatMode(mode);
        if (!List.of("Easy", "Medium", "Hard", "Challenge").contains(formattedMode)) {
            System.err.println("Invalid mode: " + mode);
            return;
        }
        System.out.println("Attempting to add score: name=" + name + ", score=" + score + ", mode=" + formattedMode);
        try (Connection conn = getConnection()) {
            if (conn == null) {
                System.err.println("Failed to add score: Database connection is null");
                return;
            }
            String difficulty = formattedMode.equals("Challenge") ? "Medium" : formattedMode;
            int isChallengeMode = formattedMode.equals("Challenge") ? 1 : 0;

            String checkSql = "SELECT score FROM leaderboard WHERE name = ? AND difficulty = ? AND is_challenge_mode = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, name);
                checkStmt.setString(2, difficulty);
                checkStmt.setInt(3, isChallengeMode);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    int existingScore = rs.getInt("score");
                    if (score > existingScore) {
                        String updateSql = "UPDATE leaderboard SET score = ? WHERE name = ? AND difficulty = ? AND is_challenge_mode = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, score);
                            updateStmt.setString(2, name);
                            updateStmt.setString(3, difficulty);
                            updateStmt.setInt(4, isChallengeMode);
                            int rowsUpdated = updateStmt.executeUpdate();
                            System.out.println("Updated score for " + name + " in mode " + formattedMode + ": " + score + " (rows affected: " + rowsUpdated + ")");
                        }
                    } else {
                        System.out.println("Score not updated: Existing score " + existingScore + " is higher than new score " + score);
                    }
                } else {
                    String insertSql = "INSERT INTO leaderboard (name, score, difficulty, is_challenge_mode) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, name);
                        insertStmt.setInt(2, score);
                        insertStmt.setString(3, difficulty);
                        insertStmt.setInt(4, isChallengeMode);
                        int rowsInserted = insertStmt.executeUpdate();
                        System.out.println("Inserted new score for " + name + " in mode " + formattedMode + ": " + score + " (rows affected: " + rowsInserted + ")");
                    }
                }
            }
            String deleteSql = "DELETE FROM leaderboard WHERE difficulty = ? AND is_challenge_mode = ? AND id NOT IN (" +
                              "SELECT id FROM (SELECT id FROM leaderboard WHERE difficulty = ? AND is_challenge_mode = ? ORDER BY score DESC LIMIT ?) AS subquery)";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setString(1, difficulty);
                pstmt.setInt(2, isChallengeMode);
                pstmt.setString(3, difficulty);
                pstmt.setInt(4, isChallengeMode);
                pstmt.setInt(5, LEADERBOARD_SIZE);
                int rowsDeleted = pstmt.executeUpdate();
                System.out.println("Pruned leaderboard for mode " + formattedMode + ": " + rowsDeleted + " rows removed");
            }
        } catch (SQLException e) {
            System.err.println("Error adding/updating score: " + e.getMessage());
        }
    }

    public List<LeaderboardEntry> getLeaderboard(String mode) {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        String formattedMode = formatMode(mode);
        if (!List.of("Easy", "Medium", "Hard", "Challenge").contains(formattedMode)) {
            System.err.println("Invalid mode: " + mode);
            return leaderboard;
        }
        String difficulty = formattedMode.equals("Challenge") ? "Medium" : formattedMode;
        int isChallengeMode = formattedMode.equals("Challenge") ? 1 : 0;
        String sql = "SELECT name, score FROM leaderboard WHERE difficulty = ? AND is_challenge_mode = ? ORDER BY score DESC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, difficulty);
            pstmt.setInt(2, isChallengeMode);
            pstmt.setInt(3, LEADERBOARD_SIZE);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                leaderboard.add(new LeaderboardEntry(rs.getString("name"), rs.getInt("score")));
            }
            System.out.println("Retrieved leaderboard for mode " + formattedMode + ": " + leaderboard.size() + " entries");
        } catch (SQLException e) {
            System.err.println("Error retrieving leaderboard: " + e.getMessage());
        }
        while (leaderboard.size() < LEADERBOARD_SIZE) {
            leaderboard.add(new LeaderboardEntry("---", 0));
        }
        return leaderboard;
    }

    public int getHighestScore(String mode) {
        String formattedMode = formatMode(mode);
        if (!List.of("Easy", "Medium", "Hard", "Challenge").contains(formattedMode)) {
            System.err.println("Invalid mode: " + mode);
            return 0;
        }
        String difficulty = formattedMode.equals("Challenge") ? "Medium" : formattedMode;
        int isChallengeMode = formattedMode.equals("Challenge") ? 1 : 0;
        String sql = "SELECT MAX(score) as max_score FROM leaderboard WHERE difficulty = ? AND is_challenge_mode = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, difficulty);
            pstmt.setInt(2, isChallengeMode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int maxScore = rs.getInt("max_score");
                System.out.println("Highest score for mode " + formattedMode + ": " + maxScore);
                return maxScore;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving highest score: " + e.getMessage());
        }
        System.out.println("No scores found for mode " + formattedMode + ", returning 0");
        return 0;
    }

    private String formatMode(String mode) {
        if (mode == null) {
            return "Medium";
        }
        return switch (mode.toUpperCase()) {
            case "EASY" -> "Easy";
            case "MEDIUM" -> "Medium";
            case "HARD" -> "Hard";
            case "CHALLENGE" -> "Challenge";
            default -> mode;
        };
    }
}
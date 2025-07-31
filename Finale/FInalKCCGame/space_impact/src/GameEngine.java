import java.util.*;

public class GameEngine {
    private static final int SCREEN_WIDTH = 1280;
    private static final int SCREEN_HEIGHT = 720;

    private Spaceship spaceship;
    private List<Enemy> enemies;
    private List<Bullet> bullets;
    private List<Obstacles> obstacles;
    private List<Wall> walls;
    private List<PowerUp> powerUps;
    private GiantUFO ufo;
    private LeaderboardManager leaderboardManager;
    private Difficulty difficulty;
    private Difficulty pendingDifficulty;
    private boolean challengeMode;
    private int score;
    private int lives;
    private GameState gameState;
    private Random random;

    public GameEngine() {
        leaderboardManager = new LeaderboardManager();
        random = new Random();
        initializeGame();
    }

    public void initializeGame() {
        spaceship = new Spaceship(SCREEN_WIDTH / 2, SCREEN_HEIGHT - 60);
        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        obstacles = new ArrayList<>();
        walls = new ArrayList<>();
        powerUps = new ArrayList<>();
        ufo = null;

        int numEnemies, baseMoveSpeed, shootProbability, lives, ufoHealth;
        if (challengeMode) {
            numEnemies = 2;
            baseMoveSpeed = switch (difficulty != null ? difficulty : Difficulty.EASY) {
                case EASY -> 1;
                case MEDIUM -> 2;
                case HARD -> 3;
            };
            shootProbability = 5;
            lives = 3;
            ufoHealth = switch (difficulty != null ? difficulty : Difficulty.EASY) {
                case EASY -> 10;
                case MEDIUM -> 20;
                case HARD -> 30;
            };
            if (score >= 100) {
                ufo = new GiantUFO(SCREEN_WIDTH / 2, 30, SCREEN_WIDTH, SCREEN_HEIGHT, baseMoveSpeed, ufoHealth);
            }
        } else {
            switch (difficulty != null ? difficulty : pendingDifficulty != null ? pendingDifficulty : Difficulty.MEDIUM) {
                case EASY:
                    numEnemies = 2;
                    baseMoveSpeed = 2;
                    shootProbability = 1;
                    lives = 5;
                    ufoHealth = 0;
                    break;
                case HARD:
                    numEnemies = 5;
                    baseMoveSpeed = 6;
                    shootProbability = 5;
                    lives = 3;
                    ufoHealth = 50;
                    if (score >= 200) {
                        ufo = new GiantUFO(SCREEN_WIDTH / 2, 30, SCREEN_WIDTH, SCREEN_HEIGHT, 3, ufoHealth);
                    }
                    break;
                case MEDIUM:
                default:
                    numEnemies = 3;
                    baseMoveSpeed = 4;
                    shootProbability = 3;
                    lives = 3;
                    ufoHealth = 0;
                    break;
            }
        }

        enemies.clear();
        for (int i = 0; i < numEnemies; i++) {
            enemies.add(new Enemy(random.nextInt(SCREEN_WIDTH - 60) + 30, 30, 
                                 SCREEN_WIDTH, SCREEN_HEIGHT, baseMoveSpeed, shootProbability, this));
        }

        score = 0;
        this.lives = lives;
        gameState = GameState.PLAYING;
    }

    public void update() {
        if (gameState != GameState.PLAYING) {
            return;
        }

        spaceship.update();

        if (challengeMode) {
            updateChallengeMode();
        } else if (difficulty == Difficulty.HARD && ufo == null && score >= 200) {
            ufo = new GiantUFO(SCREEN_WIDTH / 2, 30, SCREEN_WIDTH, SCREEN_HEIGHT, 3, 50);
        }

        if (ufo != null) {
            ufo.update();
            if (!ufo.isActive()) {
                ufo = null;
                score += 100;
            }
            if (ufo != null && ufo.shouldFire()) {
                Collections.addAll(bullets, ufo.fire());
            }
        }

        for (Enemy enemy : enemies) {
            enemy.update(score, bullets);
        }

        Iterator<Bullet> bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet bullet = bulletIter.next();
            bullet.update();
            if (!bullet.isActive()) {
                bulletIter.remove();
            }
        }

        Iterator<Obstacles> obstacleIter = obstacles.iterator();
        while (obstacleIter.hasNext()) {
            Obstacles obstacle = obstacleIter.next();
            obstacle.update(score);
            if (!obstacle.isActive()) {
                obstacleIter.remove();
            }
        }

        Iterator<Wall> wallIter = walls.iterator();
        while (wallIter.hasNext()) {
            Wall wall = wallIter.next();
            wall.update();
            if (!wall.isActive()) {
                wallIter.remove();
            }
        }

        Iterator<PowerUp> powerUpIter = powerUps.iterator();
        while (powerUpIter.hasNext()) {
            PowerUp powerUp = powerUpIter.next();
            powerUp.update();
            if (!powerUp.isActive()) {
                powerUpIter.remove();
            }
        }

        int spawnChance = switch (difficulty != null ? difficulty : Difficulty.MEDIUM) {
            case EASY -> 2;
            case MEDIUM -> 4;
            case HARD -> 6;
        };
        if (random.nextInt(1000) < spawnChance) {
            obstacles.add(new Obstacles(random.nextInt(SCREEN_WIDTH - 60) + 30, 0, 
                                       difficulty == Difficulty.HARD ? 5 : difficulty == Difficulty.MEDIUM ? 4 : 3));
        }
        if (difficulty == Difficulty.HARD && random.nextInt(1000) < 3) {
            int gapX = random.nextInt(SCREEN_WIDTH - 400) + 200;
            int gapWidth = 300;
            walls.add(new Wall(0, 0, SCREEN_WIDTH, gapX, gapWidth, 2));
        }
        if (random.nextInt(1000) < (difficulty == Difficulty.EASY ? 20 : difficulty == Difficulty.MEDIUM ? 20 : 10)) {
            powerUps.add(new PowerUp(random.nextInt(SCREEN_WIDTH - 60) + 30, 0, PowerUp.Type.EXTRA_LIFE));
        }
        if (random.nextInt(1000) < (difficulty == Difficulty.HARD ? 15 : challengeMode && difficulty == Difficulty.HARD ? 15 : 0)) {
            powerUps.add(new PowerUp(random.nextInt(SCREEN_WIDTH - 60) + 30, 0, PowerUp.Type.TRIPLE_SHOT));
        }

        checkCollisions();

        if (lives <= 0) {
            gameState = GameState.GAME_OVER;
        }
    }

    private void updateChallengeMode() {
        int numEnemies, baseMoveSpeed, shootProbability, ufoHealth;
        if (score >= 300) {
            difficulty = Difficulty.HARD;
            numEnemies = 5;
            baseMoveSpeed = 3;
            shootProbability = 5;
            ufoHealth = 30;
        } else if (score >= 100) {
            difficulty = Difficulty.MEDIUM;
            numEnemies = 3;
            baseMoveSpeed = 2;
            shootProbability = 3;
            ufoHealth = 20;
        } else {
            difficulty = Difficulty.EASY;
            numEnemies = 2;
            baseMoveSpeed = 1;
            shootProbability = 1;
            ufoHealth = 10;
        }

        if (ufo == null && score >= 100) {
            ufo = new GiantUFO(SCREEN_WIDTH / 2, 30, SCREEN_WIDTH, SCREEN_HEIGHT, baseMoveSpeed, ufoHealth);
        } else if (ufo != null && !ufo.isActive()) {
            ufo.respawn(ufoHealth);
        }

        while (enemies.size() < numEnemies) {
            enemies.add(new Enemy(random.nextInt(SCREEN_WIDTH - 60) + 30, 30, 
                                 SCREEN_WIDTH, SCREEN_HEIGHT, baseMoveSpeed, shootProbability, this));
        }
        while (enemies.size() > numEnemies) {
            enemies.remove(enemies.size() - 1);
        }
        for (Enemy enemy : enemies) {
            enemy.setBaseMoveSpeed(baseMoveSpeed);
            enemy.setShootProbability(shootProbability);
        }
    }

    private void checkCollisions() {
        Iterator<Bullet> bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet bullet = bulletIter.next();
            if (bullet.isPlayerBullet()) {
                Iterator<Enemy> enemyIter = enemies.iterator();
                while (enemyIter.hasNext()) {
                    Enemy enemy = enemyIter.next();
                    if (bullet.isColliding(enemy)) {
                        bulletIter.remove();
                        enemy.respawn();
                        score += 10;
                        break;
                    }
                }
                if (ufo != null && bullet.isColliding(ufo)) {
                    bulletIter.remove();
                    ufo.hit();
                    score += 5;
                }
            }
        }

        bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet bullet = bulletIter.next();
            if (!bullet.isPlayerBullet() && bullet.isColliding(spaceship)) {
                bulletIter.remove();
                lives--;
                break;
            }
        }

        for (Enemy enemy : enemies) {
            if (enemy.isColliding(spaceship)) {
                enemy.respawn();
                lives--;
                break;
            }
        }

        Iterator<Obstacles> obstacleIter = obstacles.iterator();
        while (obstacleIter.hasNext()) {
            Obstacles obstacle = obstacleIter.next();
            if (obstacle.isColliding(spaceship)) {
                obstacleIter.remove();
                lives--;
                break;
            }
        }

        Iterator<Wall> wallIter = walls.iterator();
        while (wallIter.hasNext()) {
            Wall wall = wallIter.next();
            if (wall.isColliding(spaceship)) {
                lives--;
                int gapX = wall.getGapX();
                int gapWidth = wall.getGapWidth();
                if (spaceship.getX() < gapX) {
                    spaceship.setX(gapX);
                } else if (spaceship.getX() > gapX + gapWidth) {
                    spaceship.setX(gapX + gapWidth);
                }
                spaceship.moveUp();
            }
            bulletIter = bullets.iterator();
            while (bulletIter.hasNext()) {
                Bullet bullet = bulletIter.next();
                if (wall.isColliding(bullet)) {
                    bulletIter.remove();
                }
            }
        }

        Iterator<PowerUp> powerUpIter = powerUps.iterator();
        while (powerUpIter.hasNext()) {
            PowerUp powerUp = powerUpIter.next();
            if (powerUp.isColliding(spaceship)) {
                powerUpIter.remove();
                if (powerUp.getType() == PowerUp.Type.EXTRA_LIFE) {
                    lives = Math.min(lives + 1, 5);
                } else {
                    spaceship.applyPowerUp(powerUp.getType());
                }
                break;
            }
        }

        if (ufo != null && ufo.isColliding(spaceship)) {
            lives--;
            ufo.respawn(ufo.getHealth());
        }
    }

    public void moveSpaceshipLeft() {
        if (gameState == GameState.PLAYING) {
            spaceship.moveLeft(SCREEN_WIDTH);
        }
    }

    public void moveRight() {
        if (gameState == GameState.PLAYING) {
            spaceship.moveRight(SCREEN_WIDTH);
        }
    }

    public void moveUp() {
        if (gameState == GameState.PLAYING) {
            spaceship.moveUp();
        }
    }

    public void moveDown() {
        if (gameState == GameState.PLAYING) {
            spaceship.moveDown(SCREEN_HEIGHT);
        }
    }

    public void shoot() {
        if (gameState == GameState.PLAYING) {
            bullets.addAll(spaceship.shoot());
        }
    }

    public void startGame() {
        if (pendingDifficulty != null) {
            difficulty = pendingDifficulty;
            pendingDifficulty = null;
        }
        initializeGame();
    }

    public void setPendingDifficulty(Difficulty diff) {
        this.pendingDifficulty = diff;
    }

    public void setChallengeMode(boolean challenge) {
        this.challengeMode = challenge;
    }

    public boolean isChallengeMode() {
        return challengeMode;
    }

    public void addScoreToLeaderboard(String name, String mode) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println("Cannot add score to leaderboard: Player name is empty or null");
            return;
        }
        if (score <= 0) {
            System.out.println("Score not added to leaderboard: Score is " + score);
            return;
        }
        String formattedMode = formatMode(mode);
        System.out.println("Adding score to leaderboard: name=" + name + ", score=" + score + ", mode=" + formattedMode);
        leaderboardManager.addScore(name, score, formattedMode);
    }

    public void resetGame() {
        score = 0;
        lives = 3;
        initializeGame();
    }

    public Spaceship getSpaceship() { return spaceship; }
    public List<Enemy> getEnemies() { return enemies; }
    public List<Bullet> getBullets() { return bullets; }
    public List<Obstacles> getObstacles() { return obstacles; }
    public List<Wall> getWalls() { return walls; }
    public List<PowerUp> getPowerUps() { return powerUps; }
    public GiantUFO getUFO() { return ufo; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public GameState getGameState() { return gameState; }
    public void setGameState(GameState state) { this.gameState = state; }
    public LeaderboardManager getLeaderboardManager() { return leaderboardManager; }

    public Difficulty getDifficulty() {
        return difficulty != null ? difficulty : Difficulty.MEDIUM;
    }

    public int getHighestScore(String mode) {
        String formattedMode = formatMode(mode);
        int highestScore = leaderboardManager.getHighestScore(formattedMode);
        System.out.println("Fetched highest score for mode " + formattedMode + ": " + highestScore);
        return highestScore;
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
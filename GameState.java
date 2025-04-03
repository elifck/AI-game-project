import java.io.Serializable;

public class GameState implements Serializable, Cloneable {
    int currentNum;
    int playerScore;
    int compScore;
    boolean isPlayerTurn;

    public GameState(int startingNumber, boolean playerStartsFirst) {
        this.currentNum = startingNumber;
        this.playerScore = 0;
        this.compScore = 0;
        this.isPlayerTurn = playerStartsFirst;
    }

    private GameState(int number, int pScore, int cScore, boolean pTurn) {
        this.currentNum = number; this.playerScore = pScore; this.compScore = cScore; this.isPlayerTurn = pTurn;
    }

    public int getCurrentNum() { return currentNum; }
    public int getPlayerScore() { return playerScore; }
    public int getCompScore() { return compScore; }
    public boolean isPlayerTurn() { return isPlayerTurn; }
    public void setCurrentNum(int num) { this.currentNum = num; }

    public boolean isGameOver() {
        // Check if the current number is less than or equal to the game end threshold
        if (currentNum <= GameLogic.GAME_END_THRESHOLD) {
            return true;
        }

        // Check if the number cannot be divided by 2 or 3
        return (currentNum % 2 != 0) && (currentNum % 3 != 0);
    }

    public boolean makeMove(int divisor) {
        if (!isMoveValid(divisor)) return false;
        if (divisor == 2) {
            if (isPlayerTurn) compScore += 2; else playerScore += 2;
        } else if (divisor == 3) {
            if (isPlayerTurn) playerScore += 3; else compScore += 3;
        }
        currentNum /= divisor;
        isPlayerTurn = !isPlayerTurn;
        return true;
    }

    public boolean isMoveValid(int divisor) {
        // Directly check if the current number is divisible by the divisor without checking if the game is over
        if (divisor != 2 && divisor != 3) return false;
        return currentNum % divisor == 0;
    }

    @Override
    public GameState clone() {
        return new GameState(this.currentNum, this.playerScore, this.compScore, this.isPlayerTurn);
    }

    @Override
    public String toString() {
        return String.format("Num: %d, Player Score: %d, Computer Score: %d, Turn: %s",
                currentNum, playerScore, compScore, (isPlayerTurn ? "Player" : "Computer"));
    }
}
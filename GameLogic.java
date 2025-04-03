import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameLogic {
    private static final Random random = new Random();
    static final int MIN_START_NUM = 10000;
    static final int MAX_START_NUM = 20000;
    static final int REQUIRED_START_NUM_COUNT = 5;
    static final int GAME_END_THRESHOLD = 10;

    public static List<Integer> getStartNumbers() {
        List<Integer> numbers = new ArrayList<>();
        int attempts = 0;
        while (numbers.size() < REQUIRED_START_NUM_COUNT && attempts < 10000) {
            int potentialNum = random.nextInt(MAX_START_NUM - MIN_START_NUM + 1) + MIN_START_NUM;
            if (potentialNum % 6 == 0 && !numbers.contains(potentialNum)) numbers.add(potentialNum);
            attempts++;
        }
        while (numbers.size() < REQUIRED_START_NUM_COUNT) {
            System.err.println("Warning: generating fallback start num.");
            int rangeMinDiv6 = (MIN_START_NUM + 5) / 6;
            int rangeMaxDiv6 = MAX_START_NUM / 6;
            int fallback = (random.nextInt(rangeMaxDiv6 - rangeMinDiv6 + 1) + rangeMinDiv6) * 6;
            if (fallback >= MIN_START_NUM && fallback <= MAX_START_NUM && !numbers.contains(fallback)){
                numbers.add(fallback);
            }
        }
        return numbers;
    }

    public static int checkWinner(GameState state) {
        if (state.playerScore > state.compScore) return 1;
        if (state.compScore > state.playerScore) return -1;
        return 0;
    }

    public static List<Move> getValidMoves(GameState state) {
        List<Move> moves = new ArrayList<>();
        if (state.isMoveValid(2)) moves.add(new Move(2));
        if (state.isMoveValid(3)) moves.add(new Move(3));
        return moves;
    }
}
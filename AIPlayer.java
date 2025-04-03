import java.util.List;

public class AIPlayer {
    public enum Algorithm { MINIMAX, ALPHA_BETA }

    final Algorithm algorithm;
    final int depth;
    long nodesChecked;
    long moveTimeMs;
    long totalNodesThisGame;
    long totalTimeThisGame;
    int movesThisGame;

    public AIPlayer(Algorithm algo, int searchDepth) {
        this.algorithm = algo;
        this.depth = searchDepth;
    }

    public long getNodesChecked() { return nodesChecked; }
    public long getMoveTimeMs() { return moveTimeMs; }
    public long getTotalNodesThisGame() { return totalNodesThisGame; }
    public long getAvgTimeThisGame() { return movesThisGame == 0 ? 0 : totalTimeThisGame / movesThisGame; }

    public void resetGameStats() {
        totalNodesThisGame = 0;
        totalTimeThisGame = 0;
        movesThisGame = 0;
    }

    public Move findBestMove(GameState currentState) {
        if (currentState.isGameOver()) {
            return null;
        }

        nodesChecked = 0;
        long startTime = System.nanoTime();
        Move chosenMove = null;
        int bestScore = Integer.MIN_VALUE;

        List<Move> possibleMoves = GameLogic.getValidMoves(currentState);
        if (possibleMoves.isEmpty()) {
            return null;
        }

        for (Move move : possibleMoves) {
            GameState nextState = currentState.clone();
            nextState.makeMove(move.getDivisor());
            int score;
            boolean isOpponentTurn = false;
            if (algorithm == Algorithm.MINIMAX) {
                score = minimax(nextState, depth - 1, isOpponentTurn);
            } else {
                score = alphaBeta(nextState, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, isOpponentTurn);
            }
            if (score > bestScore) {
                bestScore = score;
                chosenMove = move;
            }
        }

        moveTimeMs = (System.nanoTime() - startTime) / 1_000_000;
        totalNodesThisGame += nodesChecked;
        totalTimeThisGame += moveTimeMs;
        movesThisGame++;

        if (chosenMove == null && !possibleMoves.isEmpty()) {
            System.err.println("AI Warning: No preferred move? Selecting first.");
            chosenMove = possibleMoves.get(0);
        }
        return chosenMove;
    }

    private int minimax(GameState state, int currentDepth, boolean isMaximizingPlayer) {
        nodesChecked++;
        if (currentDepth == 0 || state.isGameOver()) return evaluate(state);
        List<Move> possibleMoves = GameLogic.getValidMoves(state);
        if (possibleMoves.isEmpty()) return evaluate(state);

        if (isMaximizingPlayer) {
            int maxScore = Integer.MIN_VALUE;
            for (Move move : possibleMoves) {
                GameState nextState = state.clone(); nextState.makeMove(move.getDivisor());
                maxScore = Math.max(maxScore, minimax(nextState, currentDepth - 1, false));
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (Move move : possibleMoves) {
                GameState nextState = state.clone(); nextState.makeMove(move.getDivisor());
                minScore = Math.min(minScore, minimax(nextState, currentDepth - 1, true));
            }
            return minScore;
        }
    }

    private int alphaBeta(GameState state, int currentDepth, int alpha, int beta, boolean isMaximizingPlayer) {
        nodesChecked++;
        if (currentDepth == 0 || state.isGameOver()) return evaluate(state);
        List<Move> possibleMoves = GameLogic.getValidMoves(state);
        if (possibleMoves.isEmpty()) return evaluate(state);

        if (isMaximizingPlayer) {
            int maxScore = Integer.MIN_VALUE;
            for (Move move : possibleMoves) {
                GameState nextState = state.clone(); nextState.makeMove(move.getDivisor());
                int score = alphaBeta(nextState, currentDepth - 1, alpha, beta, false);
                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) break;
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (Move move : possibleMoves) {
                GameState nextState = state.clone(); nextState.makeMove(move.getDivisor());
                int score = alphaBeta(nextState, currentDepth - 1, alpha, beta, true);
                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) break;
            }
            return minScore;
        }
    }

    private int evaluate(GameState state) {
        if (state.isGameOver()) {
            int winner = GameLogic.checkWinner(state);
            int scoreDiff = state.compScore - state.playerScore;
            if (winner == -1) return 10000 + scoreDiff;
            if (winner == 1) return -10000 + scoreDiff;
            return 0;
        }

        int scoreDiff = state.compScore - state.playerScore;
        int endProximity = Math.max(0, 100 - (state.currentNum - GameLogic.GAME_END_THRESHOLD));
        int proxFactor = 0;
        if (scoreDiff < 0) proxFactor = -endProximity / 5;
        else if (scoreDiff > 0) proxFactor = endProximity / 10;
        boolean canDiv2 = state.isMoveValid(2);
        boolean canDiv3 = state.isMoveValid(3);
        int optsBonus = (canDiv2 && canDiv3) ? 3 : 0;
        int stratBonus = 0;
        if (canDiv3) stratBonus += 1;
        if (canDiv2 && !canDiv3) stratBonus -= 1;

        return scoreDiff + proxFactor + optsBonus + stratBonus;
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class GameGUI extends JFrame implements ActionListener {

    private GameState currentGameState;
    private AIPlayer computerPlayer;
    private AIPlayer experimentAI;

    private JComboBox<Integer> numberSelector;
    private JRadioButton humanStartsRadio, computerStartsRadio;
    private JRadioButton minimaxRadio, alphaBetaRadio;
    private JButton startGameButton;
    private JButton runExperimentsButton;
    private JPanel setupPanel;
    private boolean setupComplete = false;

    private JLabel currentNumberLabel;
    private JLabel playerScoreLabel, computerScoreLabel;
    private JLabel turnLabel;
    private JButton divideBy2Button, divideBy3Button;
    private JTextArea messageArea;
    private JButton newGameButton;
    private JPanel gamePanel;
    private JPanel controlPanel;

    private static final int FIXED_AI_DEPTH = 6;
    private static final int NUM_EXPERIMENTS = 10;

    public GameGUI() {
        super("Divide and Conquer Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        setupPanel = createSetupPanel();
        gamePanel = createGamePanel();
        controlPanel = createControlPanel();

        add(setupPanel, BorderLayout.CENTER);

        pack();
        setMinimumSize(getPreferredSize());
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createSetupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Game Setup"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Choose Starting Number:"), gbc);
        numberSelector = new JComboBox<>();
        populateStartNumbers();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(numberSelector, gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Who Starts?"), gbc);
        humanStartsRadio = new JRadioButton("Player", true);
        computerStartsRadio = new JRadioButton("Computer");
        ButtonGroup startGroup = new ButtonGroup();
        startGroup.add(humanStartsRadio);
        startGroup.add(computerStartsRadio);
        JPanel playerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        playerPanel.add(humanStartsRadio);
        playerPanel.add(computerStartsRadio);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(playerPanel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Computer Algorithm:"), gbc);
        minimaxRadio = new JRadioButton("Minimax", true);
        alphaBetaRadio = new JRadioButton("Alpha-Beta");
        ButtonGroup algoGroup = new ButtonGroup();
        algoGroup.add(minimaxRadio);
        algoGroup.add(alphaBetaRadio);
        JPanel algoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        algoPanel.add(minimaxRadio);
        algoPanel.add(alphaBetaRadio);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(algoPanel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(this);
        runExperimentsButton = new JButton("Run Experiments (Depth " + FIXED_AI_DEPTH + ")");
        runExperimentsButton.addActionListener(this);
        buttonPanel.add(startGameButton);
        buttonPanel.add(runExperimentsButton);
        panel.add(buttonPanel, gbc);

        return panel;
    }
    private void populateStartNumbers() {
        numberSelector.removeAllItems();
        List<Integer> startNumbers = GameLogic.getStartNumbers();
        for (Integer num : startNumbers) {
            numberSelector.addItem(num);
        }
    }

    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Game Play"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Current Number:"), gbc);
        currentNumberLabel = new JLabel("---", JLabel.CENTER);
        currentNumberLabel.setFont(currentNumberLabel.getFont().deriveFont(Font.BOLD, 20f));
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(currentNumberLabel, gbc);

        gbc.gridy++;
        gbc.gridx = 0; gbc.gridwidth = 1;
        panel.add(new JLabel("Player Score:"), gbc);
        playerScoreLabel = new JLabel("0", JLabel.CENTER);
        gbc.gridx = 1;
        panel.add(playerScoreLabel, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Computer Score:"), gbc);
        computerScoreLabel = new JLabel("0", JLabel.CENTER);
        gbc.gridx = 3;
        panel.add(computerScoreLabel, gbc);

        gbc.gridy++;
        gbc.gridx = 0; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER;
        turnLabel = new JLabel("Turn: ---");
        turnLabel.setFont(turnLabel.getFont().deriveFont(Font.ITALIC));
        panel.add(turnLabel, gbc);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy++;
        gbc.gridx = 0; gbc.gridwidth = 2;
        divideBy2Button = new JButton("Divide by 2");
        divideBy2Button.addActionListener(this);
        panel.add(divideBy2Button, gbc);

        gbc.gridx = 2; gbc.gridwidth = 2;
        divideBy3Button = new JButton("Divide by 3");
        divideBy3Button.addActionListener(this);
        panel.add(divideBy3Button, gbc);

        gbc.gridy++;
        gbc.gridx = 0; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        messageArea = new JTextArea(6, 30);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        panel.add(scrollPane, gbc);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(this);
        newGameButton.setVisible(false);
        panel.add(newGameButton);
        return panel;
    }
    private void startGame() {
        int startNum = (Integer) Objects.requireNonNull(numberSelector.getSelectedItem());
        boolean playerStarts = humanStartsRadio.isSelected();
        AIPlayer.Algorithm algo = minimaxRadio.isSelected() ? AIPlayer.Algorithm.MINIMAX : AIPlayer.Algorithm.ALPHA_BETA;
        int depth = FIXED_AI_DEPTH;

        currentGameState = new GameState(startNum, playerStarts);
        computerPlayer = new AIPlayer(algo, depth);

        setupComplete = true;
        getContentPane().remove(setupPanel);
        add(gamePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        newGameButton.setVisible(false);

        messageArea.setText("Game started with number " + startNum + ".\nAI Difficulty (Depth): " + depth + "\n");
        computerPlayer.resetGameStats();
        updateUIState();

        if (!currentGameState.isPlayerTurn()) {
            appendMessage("Computer starts.");
            triggerComputerMove();
        } else {
            appendMessage("Player starts.");
            setPlayerControlsEnabled(true);
        }

        revalidate();
        repaint();
        pack();
    }

    private void resetForNewGame() {
        currentGameState = null;
        computerPlayer = null;
        experimentAI = null;
        setupComplete = false;

        getContentPane().remove(gamePanel);
        getContentPane().remove(controlPanel);
        add(setupPanel, BorderLayout.CENTER);

        populateStartNumbers();
        messageArea.setText("");

        revalidate();
        repaint();
        pack();
    }

    private void updateUIState() {
        if (currentGameState == null) return;

        currentNumberLabel.setText(String.valueOf(currentGameState.getCurrentNum()));
        playerScoreLabel.setText(String.valueOf(currentGameState.getPlayerScore()));
        computerScoreLabel.setText(String.valueOf(currentGameState.getCompScore()));

        if (currentGameState.isGameOver()) {
            turnLabel.setText("Game Over");
            setPlayerControlsEnabled(false);
        } else {
            turnLabel.setText("Turn: " + (currentGameState.isPlayerTurn() ? "Player" : "Computer"));
            setPlayerControlsEnabled(currentGameState.isPlayerTurn());
        }
    }

    private void appendMessage(String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            messageArea.append(message + "\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        } else {
            SwingUtilities.invokeLater(() -> {
                messageArea.append(message + "\n");
                messageArea.setCaretPosition(messageArea.getDocument().getLength());
            });
        }
    }
    private void triggerComputerMove() {
        if (currentGameState == null || currentGameState.isPlayerTurn() || currentGameState.isGameOver()) return;

        setPlayerControlsEnabled(false);
        turnLabel.setText("Turn: Computer (Thinking...)");

        SwingWorker<Move, Void> worker = new SwingWorker<>() {
            @Override
            protected Move doInBackground() {
                return computerPlayer.findBestMove(currentGameState);
            }

            @Override
            protected void done() {
                try {
                    Move chosenMove = get();
                    if (chosenMove != null) {
                        appendMessage("Computer chose: " + chosenMove +
                                " (Nodes: " + computerPlayer.getNodesChecked() +
                                ", Time: " + computerPlayer.getMoveTimeMs() + "ms)");
                        currentGameState.makeMove(chosenMove.getDivisor());
                    } else {
                        appendMessage("Computer cannot make a move. Forcing end.");
                        currentGameState.setCurrentNum(GameLogic.GAME_END_THRESHOLD);
                    }

                    updateUIState();
                    if (currentGameState.isGameOver()) {
                        endGame();
                    } else {
                        setPlayerControlsEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    appendMessage("Error during computer's turn: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void setPlayerControlsEnabled(boolean isEnabled) {
        if (isEnabled && currentGameState != null && currentGameState.isPlayerTurn()) {
            divideBy2Button.setEnabled(currentGameState.isMoveValid(2));
            divideBy3Button.setEnabled(currentGameState.isMoveValid(3));
        } else {
            divideBy2Button.setEnabled(false);
            divideBy3Button.setEnabled(false);
        }
    }

    private void handlePlayerMove(int divisor) {
        if (currentGameState == null || !currentGameState.isPlayerTurn() || currentGameState.isGameOver()) return;

        if (!currentGameState.isMoveValid(divisor)) {
            appendMessage("Invalid move: " + currentGameState.getCurrentNum() + " not divisible by " + divisor + ".");
            return;
        }

        appendMessage("Player divides by " + divisor + ".");
        currentGameState.makeMove(divisor);
        updateUIState();

        if (currentGameState.isGameOver()) {
            endGame();
        } else {
            triggerComputerMove();
        }
    }

    private void endGame() {
        setPlayerControlsEnabled(false);
        int winnerCode = GameLogic.checkWinner(currentGameState);
        String endMessage = "\n--- GAME OVER ---\n";
        endMessage += "Final Number: " + currentGameState.getCurrentNum() + "\n";
        endMessage += "Final Score -> Player: " + currentGameState.getPlayerScore() +
                " | Computer: " + currentGameState.getCompScore() + "\n";

        if (winnerCode == 1) endMessage += ">>> PLAYER WINS! <<<";
        else if (winnerCode == -1) endMessage += ">>> COMPUTER WINS! <<<";
        else endMessage += ">>> IT'S A DRAW! <<<";

        appendMessage(endMessage);
        newGameButton.setVisible(true);
    }
    private void runExperiments() {
        startGameButton.setEnabled(false);
        runExperimentsButton.setEnabled(false);
        newGameButton.setVisible(false);
        if (divideBy2Button != null) divideBy2Button.setEnabled(false);
        if (divideBy3Button != null) divideBy3Button.setEnabled(false);

        appendMessage("\n--- Running Experiments ---");
        appendMessage("This might take a while...");

        AIPlayer.Algorithm algo = minimaxRadio.isSelected() ? AIPlayer.Algorithm.MINIMAX : AIPlayer.Algorithm.ALPHA_BETA;
        int depth = FIXED_AI_DEPTH;
        int startNum = (Integer) Objects.requireNonNull(numberSelector.getSelectedItem());

        SwingWorker<String, String> experimentWorker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                publish("Starting " + NUM_EXPERIMENTS + " games for " + algo + " at fixed depth " + depth + "...");
                experimentAI = new AIPlayer(algo, depth);
                AIPlayer opponentAI = new AIPlayer(algo, depth);

                int playerWins = 0;
                int compWins = 0;
                int draws = 0;
                long totalNodesSum = 0;
                long totalAvgTimeSum = 0;

                for (int i = 1; i <= NUM_EXPERIMENTS; i++) {
                    boolean player1Starts = (i % 2 == 1);
                    GameState expState = new GameState(startNum, player1Starts);
                    experimentAI.resetGameStats();
                    opponentAI.resetGameStats();

                    while (!expState.isGameOver()) {
                        AIPlayer currentTurnAI = expState.isPlayerTurn() ? opponentAI : experimentAI;
                        Move move = currentTurnAI.findBestMove(expState);
                        if (move != null) {
                            expState.makeMove(move.getDivisor());
                        } else {
                            expState.setCurrentNum(GameLogic.GAME_END_THRESHOLD);
                        }
                    }

                    int winner = GameLogic.checkWinner(expState);
                    if (winner == 1) playerWins++;
                    else if (winner == -1) compWins++;
                    else draws++;

                    totalNodesSum += experimentAI.getTotalNodesThisGame();
                    totalAvgTimeSum += experimentAI.getAvgTimeThisGame();

                    publish("Game " + i + " finished. Winner: " + (winner == 1 ? "Opponent" : (winner == -1 ? "ExperimentAI" : "Draw")) +
                            ", Nodes: " + experimentAI.getTotalNodesThisGame() +
                            ", AvgTime: " + experimentAI.getAvgTimeThisGame() + "ms");
                }

                long avgNodes = (NUM_EXPERIMENTS == 0) ? 0 : totalNodesSum / NUM_EXPERIMENTS;
                long avgTime = (NUM_EXPERIMENTS == 0) ? 0 : totalAvgTimeSum / NUM_EXPERIMENTS;

                return String.format(
                        "\n--- Experiment Results (%d Games) ---\n" +
                                "Algorithm: %s, Fixed Depth: %d\n" +
                                "Experiment AI Wins: %d\n" +
                                "Opponent AI Wins: %d\n" +
                                "Draws: %d\n" +
                                "Avg Nodes Visited per Game (by Exp AI): %d\n" +
                                "Avg Move Time per Game (Avg for Exp AI): %d ms\n" +
                                "--------------------------------------",
                        NUM_EXPERIMENTS, algo, depth,
                        compWins, playerWins, draws, avgNodes, avgTime
                );
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    appendMessage(message);
                }
            }

            @Override
            protected void done() {
                startGameButton.setEnabled(true);
                runExperimentsButton.setEnabled(true);
                if (setupComplete && currentGameState != null) {
                    updateUIState();
                    newGameButton.setVisible(currentGameState.isGameOver());
                }

                try {
                    String finalResults = get();
                    appendMessage(finalResults);
                    JOptionPane.showMessageDialog(GameGUI.this,
                            finalResults,
                            "Experiment Results",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    appendMessage("\nError during experiments: " + e.getMessage());
                    JOptionPane.showMessageDialog(GameGUI.this,
                            "Error running experiments: " + e.getMessage(),
                            "Experiment Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        experimentWorker.execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == startGameButton) {
            startGame();
        } else if (source == newGameButton) {
            resetForNewGame();
        } else if (source == divideBy2Button) {
            handlePlayerMove(2);
        } else if (source == divideBy3Button) {
            handlePlayerMove(3);
        } else if (source == runExperimentsButton) {
            runExperiments();
        }
    }
}
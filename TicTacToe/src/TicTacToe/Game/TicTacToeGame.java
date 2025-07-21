package TicTacToe.Game;

import TicTacToe.Player.Player;
import TicTacToe.Player.PlayerFactory;
import TicTacToe.Player.PlayerStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicTacToeGame implements BoardGame{

    private final Board board;
    private final int numberOfPlayers;
    private final List<Player> players;
    private int currentPlayerIndex;
    private final GameContext gameContext;
    public TicTacToeGame(int numberOfPlayers, List<PlayerStrategy> strategies, List<Character> symbols, int size){
        String sessionId = UUID.randomUUID().toString();
        String logFile = "logs/game_" + sessionId + ".log";
        this.numberOfPlayers = numberOfPlayers;
        board = new Board(size);
        board.addListener(new FileLoggerListener(logFile));
        players = new ArrayList<>();
        for(int i=0;i<numberOfPlayers;i+=1){
            players.add(PlayerFactory.createPlayer(symbols.get(i),strategies.get(i)));
        }
        currentPlayerIndex = 0;
        gameContext = new GameContext(players.get(currentPlayerIndex));
    }

    @Override
    public void play() {
        do {
            // print the current state of the game
            board.printBoard();
            // current player makes the move
            Position move = players.get(currentPlayerIndex).getPlayerStrategy().makeMove(board);
            board.makeMove(move, players.get(currentPlayerIndex).getSymbol());
            // checks game state for win/draw
            board.checkGameState(gameContext);
            System.out.println(gameContext.getCurrentState());
            if(!gameContext.isGameOver()) {
                switchPlayer();
            }
        } while (!gameContext.isGameOver());
        announceResult();
    }

    @Override
    public void switchPlayer() {
        currentPlayerIndex = (currentPlayerIndex+1)%numberOfPlayers;
        gameContext.setCurrentState(new InProgressState(players.get(currentPlayerIndex)));
        board.notifyGameStateChanged(gameContext.getCurrentState());
    }

    @Override
    public void announceResult() {
        GameState state = gameContext.getCurrentState();
        if (state instanceof WonState) {
            System.out.println("Player "+state.getPlayer().getSymbol()+" wins!");
        } else if (state instanceof DrawState) {
            System.out.println("It's a draw!");
        }
    }
}
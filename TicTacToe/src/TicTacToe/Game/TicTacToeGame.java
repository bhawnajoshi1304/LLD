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
    private final List<GameEventListener> listeners;
    public TicTacToeGame(int numberOfPlayers, List<PlayerStrategy> strategies, List<Character> symbols, int size){
        String sessionId = UUID.randomUUID().toString();
        String logFile = "logs/game_" + sessionId + ".log";
        this.numberOfPlayers = numberOfPlayers;
        board = new Board(size);
        addListener(new FileLoggerListener(logFile));
        players = new ArrayList<>();
        for(int i=0;i<numberOfPlayers;i+=1){
            players.add(PlayerFactory.createPlayer(symbols.get(i),strategies.get(i)));
        }
        currentPlayerIndex = 0;
        gameContext = new GameContext(players.get(currentPlayerIndex));
        this.listeners = new ArrayList<>();
    }

    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }
    // Notifies users whenever a move has been made
    public void notifyMoveMade(Position position, Character symbol) {
        for (GameEventListener listener : listeners) {
            listener.onMoveMade(position, symbol);
        }
    }
    // Notifies user on change of game state
    public void notifyGameStateChanged(GameState state) {
        for (GameEventListener listener : listeners) {
            listener.onGameStateChanged(state);
        }
    }
    @Override
    public void play() {
        do {
            // print the current state of the game
            board.printBoard();
            // current player makes the move
            Position move = players.get(currentPlayerIndex).getPlayerStrategy().makeMove(board);
            board.makeMove(move, players.get(currentPlayerIndex).getSymbol());
            notifyMoveMade(move, players.get(currentPlayerIndex).getSymbol());
            // checks game state for win/draw
            if(board.hasGameStateChanged(gameContext))
                notifyGameStateChanged(gameContext.getCurrentState());

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
        notifyGameStateChanged(gameContext.getCurrentState());
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
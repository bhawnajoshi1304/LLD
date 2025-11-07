import Game.Engine;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello from chess game!");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Player 1 name: ");
        String player1Name = scanner.nextLine().trim();

        System.out.print("Enter Player 2 name: ");
        String player2Name = scanner.nextLine().trim();

        Engine game = new Engine();
        try {
            game.startGame(player1Name, player2Name);
        }catch (RuntimeException e){
            System.out.println("Runtime error occurred: "+ e.getMessage());
            throw e;
        }
    }
}
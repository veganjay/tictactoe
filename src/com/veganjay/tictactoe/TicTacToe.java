package com.veganjay.tictactoe;

import java.util.Scanner;

import com.veganjay.tictactoe.TicTacToeBoard.Piece;

/**
 * Tic Tac Toe Game
 * 
 * Computer AI using MiniMax with beta-alpha pruning and depth specification
 * @author jason
 */
public class TicTacToe {
	public static final Piece START_PLAYER = Piece.X;
	
	// Member variables
	private Scanner input;
	private TicTacToeBoard board;
	
	private Piece currentPlayer;
	private Piece humanPiece = START_PLAYER;
	
	public TicTacToe() {
		this.input=new Scanner(System.in);	
		this.board = new TicTacToeBoard();
	}
	
	private int getChoice() {
		String choice;
		int    col;
				
		// Read from input
		choice = input.next();
		System.out.println();
		
		try {
			col = Integer.parseInt(choice);
		} catch (NumberFormatException e) {
			System.out.println("Enter (1-9): ");
			return getChoice();
		}
		return col;
	}
	
	private void test1() {
//		board.addPiece(2, Piece.X);
//		board.addPiece(5, Piece.O);
//		board.addPiece(1, Piece.X);
//		board.addPiece(3, Piece.O);
//		board.addPiece(6, Piece.X);
//		board.addPiece(9, Piece.O);
	}
	public void mainLoop() {
		// Initialize with start player
		currentPlayer = START_PLAYER;
		TicTacToeAI ai = new TicTacToeAI(Piece.O);

		test1();
		
		// Display the board;
		board.printBoard();
		
		while (true) {		
			int spaceNum;
			if (currentPlayer == humanPiece) {
				// Get the choice
				System.out.print("Enter Space Number (1-9): ");
				spaceNum = getChoice();							
			} else {
				spaceNum = ai.getMove(board); 
				//spaceNum = ai.getComputerMove(board);
				System.out.println("Computer moves to " + spaceNum);
			}

			// Add the piece to the board
			board.addPiece(spaceNum, currentPlayer);
			
			// Display the board;
			board.printBoard();

			// Check for winner
			if (board.isWinner(currentPlayer)) {
				System.out.println(currentPlayer + " is the winner!");
				break;
			}
			
			// Check for tie
			if (board.isFull()) {
				System.out.println("Tie game!");
				break;
			}
						
			// Switch players
			this.switchPlayers();
			
		} // End while

	} // End main loop
	
	private void switchPlayers() {
		if (currentPlayer == Piece.X) {
			currentPlayer = Piece.O;
		} else {
			currentPlayer = Piece.X;
		}
	}
	public static void main (String [] args) {
		TicTacToe game = new TicTacToe();
		game.mainLoop();
	}
}

package com.veganjay.tictactoe;

import java.util.HashSet;
import java.util.Set;

import com.veganjay.tictactoe.TicTacToeBoard.Piece;

public class TicTacToeAI {
	
	// Constants
	public static final int MINMAX_DEPTH = 9;
	
	// Debug variable
	private boolean debugEnabled = false;
	
	// Member variables
	private TicTacToeBoard board;
	private Piece computerPiece;
	private Piece playerPiece;

	// Inner class
	private class Node {
		private TicTacToeBoard board;
		private Piece currentPlayer;
		private int   row;
		private int   column;
		
		public Node(TicTacToeBoard board, Piece currentPlayer) {
			this.board    = new TicTacToeBoard(board);
			this.currentPlayer = currentPlayer;
		}
		
		public Node(TicTacToeBoard board, Piece currentPlayer, int row, int column) {
			this(board, currentPlayer);
			this.row = row;
			this.column = column;
		}

		public void debug() {
			this.board.printBoard();
		}
		
		public boolean isTerminalNode() {
			boolean terminal = false;
			
			if (this.board.isFull()) {
				terminal = true;
			} else if (this.board.isWinner(Piece.O)) {
				terminal = true;
			} else if (this.board.isWinner(Piece.X)) {
				terminal = true;
			}
			return terminal;
		}
		private Piece getNextPlayer() {
			if (this.currentPlayer == Piece.X) {
				return Piece.O;
			} else {
				return Piece.X;
			}
		}
		
		/**
		 * Determine the possible next moves and add to the tree
		 */
		public Set<Node> getChildren() {
			Set<Node> children = new HashSet<Node>();
			
			for (int i = 0; i < TicTacToeBoard.SIZE; i++) {
				for (int j = 0; j < TicTacToeBoard.SIZE; j++) {
					// Skip moves where the space is already occupied
					if (this.board.isOccupied(i, j)) {
						continue;
					}
					// Create a new board containing this move possibility
					TicTacToeBoard childBoard = new TicTacToeBoard(board);
					childBoard.addPiece(i, j, currentPlayer);

					//debug("Creating child with space " + i + ", " + j);
					
					// Store the child in the tree
					Node child = new Node(childBoard, getNextPlayer(), i, j);
					children.add(child);
				}
			}
			return children;
		}

		public int getObjectiveValue() {
			int objValue = 0;
			
			if (this.board.isWinner(playerPiece)) {
				objValue = -1;
			} else if (this.board.isWinner(computerPiece)) {
				objValue = 1;
			}
			return objValue;
		}
		
		public int getSpaceNum() {
			return TicTacToeBoard.getPlaceNum(row, column);
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			sb.append(row);
			sb.append(",");
			sb.append(column);
			sb.append("]");
			sb.append(" ");
			sb.append(this.getSpaceNum());
			return sb.toString();
		}
	} // End class Node
	
	public TicTacToeAI(Piece computerPiece) {
		this.computerPiece = computerPiece;
		
		if (computerPiece == Piece.X) {
			this.playerPiece = Piece.O;
		} else {
			this.playerPiece = Piece.X;
		}
	}

	public void enableDebug() {
		this.debugEnabled = true;
	}
	
	public void disableDebug() {
		this.debugEnabled = false;
	}
	
	private void debug(String s) {
		if (this.debugEnabled) {
			System.out.println(s);
		}
	}
	
	private int minimax(Node node, int depth, int alpha, int beta, boolean needMax) {
		if (depth <= 0 || node.isTerminalNode()) {
			return node.getObjectiveValue();
		}

		for (Node child : node.getChildren()) {
			int score = minimax(child, depth-1, alpha, beta, !needMax);

			if (needMax) {  // Do Max
				if (score > alpha) {
					alpha = score;
				}
				if (beta <= alpha) {
					break;
				}
			} else { // Do Min
				if (score < beta) {
					beta = score;
				}
				if (beta <= alpha) {
					break;
				}
			}
		}
		
		return needMax ? alpha : beta;
	}
		
	public int getMove(TicTacToeBoard other) {
		int spaceNum = 0;
		int score = 0;
		int bestScore = Integer.MIN_VALUE;
		Node bestMove = null;
		Node node = new Node(other, computerPiece);
		
		Set<Node> children = node.getChildren();

		for (Node child : children) {
			// Use Minimax to get computer move
			score = this.minimax(child, MINMAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
			
			// Display the move and score, for debugging
			debug(child.getSpaceNum() + ", score=" + score);
			
			// Chose the best move
			if (score > bestScore) {
				bestScore = score;
				bestMove  = child;
			}
		}

		debug("bestMove " + bestMove + ", bestScore = " + bestScore);
		if (bestMove != null) {
			spaceNum = bestMove.getSpaceNum();
		}
		return spaceNum;
	}

	@Deprecated
	public int getComputerMove(TicTacToeBoard other) {
		int spaceNum    = 0;
		int maxObjValue = Integer.MIN_VALUE;

		board = new TicTacToeBoard(other);

		for (int i = 0; i < TicTacToeBoard.SIZE; i++) {
			for (int j = 0; j < TicTacToeBoard.SIZE; j++) {

				if (board.isOccupied(i, j)) {
					continue;
				}
				if (spaceNum == 0) {
					spaceNum = TicTacToeBoard.getPlaceNum(i, j);
				}
				board.addPiece(i, j, computerPiece);
				int objValue = this.getObjectiveValue(board);
				int space = TicTacToeBoard.getPlaceNum(i, j);
				
				// debug(space + " " + objValue);
				if (objValue > maxObjValue) {
					maxObjValue = objValue;
					spaceNum = space;
				}
				board.removePiece(i, j);
			}
		}
		
		debug("objValue = " + maxObjValue);
		
		return spaceNum;
	}

	
	public int getObjectiveValue(TicTacToeBoard board) {
		int objValue = 0;
		Piece spaces[] = new Piece[3];
		
		if (board.isWinner(computerPiece)) {
			return Integer.MAX_VALUE;
		} else if (board.isWinner(playerPiece)) {
			return Integer.MIN_VALUE;
		}

		// Vertical
		for (int col = 0; col < TicTacToeBoard.SIZE; col++) {
			spaces[0] = board.getPiece(0, col);
			spaces[1] = board.getPiece(1, col);
			spaces[2] = board.getPiece(2, col);
			
			objValue = objValue + getObjectiveValue(spaces);
		}		

		// Horizontal
		for (int row = 0; row < TicTacToeBoard.SIZE; row++) {
			spaces[0] = board.getPiece(row, 0);
			spaces[1] = board.getPiece(row, 1);
			spaces[2] = board.getPiece(row, 2);

			objValue = objValue + getObjectiveValue(spaces);
		}		
		
		// Diagonals
		spaces[0] = board.getPiece(0, 0);
		spaces[1] = board.getPiece(1, 1);
		spaces[2] = board.getPiece(2, 2);

		objValue = objValue + getObjectiveValue(spaces);
		
		// Check Diagonal Win
		spaces[0] = board.getPiece(0, 2);
		spaces[1] = board.getPiece(1, 1);
		spaces[2] = board.getPiece(2, 0);

		objValue = objValue + getObjectiveValue(spaces);

		return objValue;
	}
	
	public int getObjectiveValue(Piece spaces[]) {
		int objValue = 0;
		
		int computerPieces = 0;
		int playerPieces   = 0;
		int emptySpaces    = 0;

		// Count the number of pieces
		for (Piece space: spaces) {
			if (space == computerPiece) {
				computerPieces++;
			} else if (space == playerPiece) {
				playerPieces++;
			} else if (space == Piece.EMPTY) {
				emptySpaces++;
			}
		}

		// Determine a value depending on the threat level
		if (computerPieces == 3) {
			objValue = Integer.MAX_VALUE;
		} else if (playerPieces == 3) {
			objValue = Integer.MIN_VALUE;
		} else if (computerPieces == 2 && emptySpaces == 1) {
			objValue = 10;
		} else if (playerPieces == 2 && emptySpaces == 1) {
			objValue = -50;
		}
		
		return objValue;
	}
	
}

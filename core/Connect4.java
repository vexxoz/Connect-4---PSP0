/**
* Connect4 game class that runs and manages game moves and win conditions 
*  
* @author Blake Caldwell
* @version 1.0
*/
package core;
public class Connect4 {
	
	/** The final variable for the player X char */
	private final char PLAYER_X = 'X';
	/** The final variable for the player O char */
	private final char PLAYER_O = 'O';
	/** The final char for the empty space*/
	private final char EMPTY = ' ';
	/** The char 2d array for the game board */
	private char[][] board;
	/** The char variable for who's turn it is*/
	private char turn;
	
	/**
	 * The Constructor for the Game object which sets all spots to the Empty char and sets
	 * the game to start with player x. 
	 */
	public Connect4() {
		board = new char[7][6];
		for(int i=0;i<7;i++) {
			for(int j = 0;j<6;j++) {
				board[i][j] = EMPTY;
			}
		}
		turn = PLAYER_X;
	}
	
	/**
	 * Method to play a move in a column
	 * @param col Which column to play a move in
	 * @return Returns the row number of the move that was played
	 */
	public int playMove(int col) {
		col--;
		if(checkMove(col)) {
			int index = 5;
			while(index > 0 && board[col][index] != EMPTY) {
				index--;
			}
			board[col][index] = turn;
			return index;
		}
		return -1;
	}
	
	/**
	 * Method to return the board 2d array
	 * @return Returns the address to the 2d array which stores the game state
	 */
	public char[][] getBoard() {
		return board;
	}
	
	/**
	 * Check if a player has won the game
	 * @return Returns boolean if player has won the game
	 */
	public boolean checkWin() {
		// for each spot
		for(int i=0;i<7;i++) {
			for(int k=0;k<6;k++) {
				// check horitonal wins
				if(i<4) {
					if(board[i][k]==turn 
					   && board[i+1][k]==turn
					   && board[i+2][k]==turn
					   && board[i+3][k]==turn) {return true;}
				}
				// check vertical wins
				if(k<3) {
					if(board[i][k]==turn 
					   && board[i][k+1]==turn
					   && board[i][k+2]==turn
					   && board[i][k+3]==turn) {return true;}
				}
				// check down-right diagonal wins
				if(i<4 && k<3) {
					if(board[i][k]==turn 
							   && board[i+1][k+1]==turn
							   && board[i+2][k+2]==turn
							   && board[i+3][k+3]==turn) {return true;}
				}
				// check up-right
				if(i<4 && k > 2) {
					if(board[i][k]==turn 
							   && board[i+1][k-1]==turn
							   && board[i+2][k-2]==turn
							   && board[i+3][k-3]==turn) {return true;}
				}
			}
		}
		return false;
	}
	
	/**
	 * Method that checks if the move was valid
	 * @param col which column the player is trying to move
	 * @return Returns boolean if the play is valid or not
	 */
	private boolean checkMove(int col) {
		int openSpots = 0;
		if(0 <= col && col < 7) {
			for(int i=0;i<6;i++) {
				if(board[col][i] == EMPTY) {
					openSpots++;
				}
			}
		}
		if(openSpots>0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Method that checks if the game is a tie
	 * @return Returns boolean if the game is a tie
	 */
	public boolean checkTie() {
		int count = 0;
		for(int i=0;i<7;i++) {
			for(int k=0;k<6;k++) {
				if(board[i][k] != EMPTY) { count++; }
			}
		}
		if(count == 42) { return true; }
		return false;
	}
	
	/**
	 * Method that gets the value at the specified index
	 * @param col The col being looked at
	 * @param row The row being looked at
	 * @return Returns the value stored at the (col,row) location
	 */
	public char getIndex(int col, int row) {
		return board[col][row];
	}
	
	/**
	 * Method that gets what turn it is
	 * @return Returns the char of who's turn it is
	 */
	public char getTurn() {
		return turn;
	}
	
	/**
	 * Method to change the turn in the game. Assigns the 
	 * value of the player to the turn variable
	 */
	public void changeTurn() {
		if(turn == PLAYER_X) {
			turn = PLAYER_O;
			return;
		}
		turn = PLAYER_X;
	}
}
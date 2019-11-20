package core;

public class Connect4ComputerPlayer extends Connect4 {
	
	// the most recent row play;
	private int col;
	
	/**
	 * A default constructor for this class. Calls the parent constructor for instantiation.
	 */
	public Connect4ComputerPlayer() {
		super();
		col=-1;
	}

	
	/**
	 * Generates a random number within range of the board and plays a move at that column.
	 * @return Returns the row number of the move that was played
	 */
	public int calculateMove() {
		// while col if full keep trying new rows
		int move;
		int colChoice;
		while(true) {
			// int from 1-7
			colChoice = (int) ((Math.random()*7 )+1);
			move = playMove(colChoice);
			// if move is valid
			if(move >= 0) {
				// exit loop return
				break;
			}
			//else try new col
		}
		col = colChoice-1;
		return move;
	}
	
	/**
	 * A way to get the col that the computer player last played at to be able to
	 * 	update the game board for gui
	 * @return Returns the col number of the move that was played
	 */
	public int getLastCol() {
		return col;
	}
}

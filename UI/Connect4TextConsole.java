/**
* The UI class to display and interact with the Connect4 Game
* 
* @author Blake Caldwell
* @version 1.0
*/
package UI;

import java.util.Scanner;

import core.*;

public class Connect4TextConsole {

	/**
	 * The instance variable for the Connect4 Game object
	 */
	private static Connect4 myGame;
	
	/**
	 * The main method to run the UI and loop for input
	 * @param args The arguments from the console. Method takes none.
	 */
	public static void main(String[] args) {
		
		String choice;
		Scanner newScan;
		
		// loop for game mode selection
		while(true) {
			System.out.println("Which game mode would you like to play? (C = against AI, P = single player)");
			newScan = new Scanner(System.in);
			if(newScan.hasNext()) {
				choice = (newScan.next()).toLowerCase();
				if(choice.equals("p") || choice.equals("c")) {
					break;
				}
			}
			System.out.println("Not a valid game choice, please try again!");
		}

		// creates the correct game class
		if(choice.equals("c")) {
			myGame = new Connect4ComputerPlayer();
		}else {
			myGame = new Connect4();
		}
		
		// creates loop for game 
		while(true) {
			// displays board
			display();
			// displays CLI
			System.out.println("Current Turn: " + myGame.getTurn());
			System.out.println("Select Column To Play (1-7): ");
			
			int inputCol;
			// loop for correct column input
			while(true) {
				newScan = new Scanner(System.in);
				if(newScan.hasNextInt()) {
					inputCol = newScan.nextInt();
					break;
				}
				System.out.println("Not a valid int please try again!");
				System.out.println("Select Column To Play (1-7): ");
			}
			// if the input is within bounds
			if(1<=inputCol && inputCol <=7) {
				// if the move was played successfully
				if(myGame.playMove(inputCol) >= 0) {
					// if game was won
					if(myGame.checkWin()) {
						System.out.println("\n\n---------------------");
						display();
						System.out.println(myGame.getTurn() + " Wins the Game!");
						break;
					}
					// if game is a tie
					if(myGame.checkTie()) {
						System.out.println("\n\n---------------------");
						display();
						System.out.println("Its A Tie!");
						break;	
					}
					
					// change turn
					myGame.changeTurn();
					
					// AI TURN ----------------
					if(choice.equals("c")) {// if AI game play AI turn
						((Connect4ComputerPlayer) myGame).calculateMove();
						// if game was won
						if(myGame.checkWin()) {
							System.out.println("\n\n---------------------");
							display();
							System.out.println(myGame.getTurn() + " Wins the Game!");
							break;
						}
						// if game is a tie
						if(myGame.checkTie()) {
							System.out.println("\n\n---------------------");
							display();
							System.out.println("Its A Tie!");
							break;	
						}
						// change back to players turn
						myGame.changeTurn();
					}
				}else {//column is full play could not be completed successfully
					System.out.println("Column Full try a different Column!");
				}
			}else {// number input not within range of the game board
				System.out.println("Number not within 1-7! Please Try Again!");
			}
		}
		newScan.close();
	}
	
	/**
	 * The method to display the contents of the game board
	 */
	public static void display() {
		for(int i=0;i<6;i++) {
			for(int k=0;k<7;k++) {
				System.out.print("|");
				System.out.print(myGame.getIndex(k, i));
				
			}
			System.out.print("|");
			System.out.println("");
		}

	}

}

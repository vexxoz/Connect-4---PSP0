package core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import UI.Connect4GUI.Cell;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Connect4Server extends Application implements Connect4Constants{


	
	 private static int threadNumber = 1;
	 
	@Override
	public void start(Stage primaryStage) throws Exception {
	    TextArea taLog = new TextArea();

	    // Create a scene and place it in the stage
	    Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
	    primaryStage.setTitle("Connect4 Server"); // Set the stage title
	    primaryStage.setScene(scene); // Place the scene in the stage
	    primaryStage.show(); // Display the stage
	    
	    // call method to branch a thread to match make
	    initiateMatchMaking(taLog);
	    
	}
	
	public static void initiateMatchMaking(TextArea taLog) {
		// new thread to create a match while allowing the gui to run
	    new Thread(()->{
	        try {
	            // Create a server socket
	            ServerSocket serverSocket = new ServerSocket(8000);
	            Platform.runLater(() -> taLog.appendText(new Date() +
	              ": Server started at socket 8000\n"));
	      
	            // Infinite loop to match make for as long as server is running
	            while (true) {
	              Platform.runLater(() -> taLog.appendText(new Date() +
	                ": Wait for players to join session " + threadNumber + '\n'));
	      
	              // Connect to player 1
	              Socket player1 = serverSocket.accept();
	      
	              Platform.runLater(() -> {
	                taLog.appendText(new Date() + ": Player 1 joined session " 
	                  + threadNumber + "from IP: "+player1.getInetAddress().getHostAddress()+"\n");
	              });
	      
	              // Notify that the player is Player 1
	              new DataOutputStream(
	                player1.getOutputStream()).writeInt(PLAYER1);
	      
	              // Connect to player 2
	              Socket player2 = serverSocket.accept();
	      
	              Platform.runLater(() -> {
	                taLog.appendText(new Date() + ": Player 2 joined session " 
	                  + threadNumber + "from IP: "+player2.getInetAddress().getHostAddress()+"\n");
	              });
	      
	              // Notify that the player is Player 2
	              new DataOutputStream(
	                player2.getOutputStream()).writeInt(PLAYER2);
	      
	              // Display this session and increment session number
	              Platform.runLater(() -> 
	                taLog.appendText(new Date() + 
	                  ": Start a game for session " + threadNumber++ + '\n'));
	      
	              // Launch a new thread for this session of two players
	              new Thread(new StartGame(player1, player2)).start();
	            }
	          }
	          catch(IOException ex) {
	            ex.printStackTrace();
	          }
	    }).start();
	}


	static class StartGame implements Runnable, Connect4Constants{
		private Socket player1;
		private Socket player2;
		
	    private DataInputStream fromPlayer1;
	    private DataOutputStream toPlayer1;
	    private DataInputStream fromPlayer2;
	    private DataOutputStream toPlayer2;

	    // declaration for connect4 game class
	    private Connect4 game;
	    
	    public StartGame(Socket player1, Socket player2) {
	    	this.player1 = player1;
	    	this.player2 = player2;
			// creates a game object to save the reuse of code
			// and maintains a level of abstraction
	    	game = new Connect4();
	    }
	    
		/**
		 * Class to be instantiated in a new thread which handles a game between two players
		 */
		public void run() {
			try {
				//Create data input and output streams
				fromPlayer1 = new DataInputStream(player1.getInputStream());
				toPlayer1 = new DataOutputStream(player1.getOutputStream());
				fromPlayer2 = new DataInputStream(player2.getInputStream());
				toPlayer2 = new DataOutputStream(player2.getOutputStream());
		
				// Write anything to notify player 1 to start
				// This is just to let player 1 know to start
				toPlayer1.writeInt(1);
				
				int row; // row of the play
				int col; // col of the play
				boolean valid = false;
				// Continuously serve the players and determine and report
				// the game status to the players
				while (true) {
					
					valid = false;
					/////////// PLAYER 1 INTERACTION ////////////
					// PLAYER 2 IS WAITING IN THIS SECTION
					
					// while player move is not valid
					while(!valid) {
						// wait for move from player 1
						col = fromPlayer1.readInt();
						// row = the row of the successful move
						row = game.playMove(col); 
						System.out.println("run(): "+ col);
						if(row > -1) {
							if(game.checkWin()) {
					            toPlayer1.writeInt(PLAYER1_WON);
					            toPlayer2.writeInt(PLAYER1_WON);
					            sendMove(toPlayer2, col-1, row);
					            break; // Break the loop
							}if(game.checkTie()) {
					            toPlayer1.writeInt(TIE);
					            toPlayer2.writeInt(TIE);
					            sendMove(toPlayer2, col-1, row);
							}else {
								game.changeTurn();
								// tells player 1 move was valid
								toPlayer1.writeInt(VALID);
								valid = true;
								// sends player 1 the row
								toPlayer1.writeInt(row);
								// send player 2 the code to make their move
								toPlayer2.writeInt(CONTINUE);
								sendMove(toPlayer2, col-1, row);
							}
						}else {
							toPlayer1.writeInt(FULL);
							valid = false;
						}
					}
					
					/////////// PLAYER 2 INTERACTION ////////////
					// PLAYER 1 IS WAITING IN THIS SECTION
					valid = false;
					
					// while player move is not valid
					while(!valid) {
						// wait for move from player 2
						col = fromPlayer2.readInt();
						// row = the row of the successful move
						row = game.playMove(col); 
						if(row > -1) {
							if(game.checkWin()) {
					            toPlayer1.writeInt(PLAYER2_WON);
					            toPlayer2.writeInt(PLAYER2_WON);
					            sendMove(toPlayer1, col-1, row);
					            break; // Break the loop
							}if(game.checkTie()) {
					            toPlayer1.writeInt(TIE);
					            toPlayer2.writeInt(TIE);
					            sendMove(toPlayer1, col-1, row);
							}else {
								game.changeTurn();
								// tells player 2 move was valid
								toPlayer2.writeInt(VALID);	
								valid = true;
								// send player 1 the code to make their move
								toPlayer1.writeInt(CONTINUE);
								sendMove(toPlayer1, col-1, row);
							}
						}else {
							// indicate that the col they tried to play was full
							toPlayer2.writeInt(FULL);
							valid = false;
						}			
					}
					
				}// ends while loop
			}catch(IOException ex) {
				ex.printStackTrace();
			}
		}// ends run method
		
		private void sendMove(DataOutputStream toPlayer, int col, int row) throws IOException {
			System.out.println("SendMOVE(): "+ col);
			toPlayer.writeInt(col);
			toPlayer.writeInt(row);
		}
		
		
	}// ends class
	
	
  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) {
    launch(args);
  }
}

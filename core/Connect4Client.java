package core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import UI.Connect4GUI.Cell;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Connect4Client extends Application implements Connect4Constants{

	private static Cell[][] cell = new Cell[7][6];
	
	// Create and initialize a status label
	private Label lblStatus = new Label("Welcome to new Game!");
	
	// Input and output streams from/to server
	private DataInputStream fromServer;
	private DataOutputStream toServer;	
	
	// Host name or ip
	private String host = "localhost";	
	
	  // Indicate the token for the player
	  private char myToken = ' ';

	  // Indicate the token for the other player
	  private char otherToken = ' ';	
	  
	  // Indicate whether the player has the turn
	  private boolean myTurn = false;	  
	  
	  // Continue to play?
	  private boolean continueToPlay = true;	  
	  // Wait for the player to mark a cell
	  private boolean waiting = true;
	  
	  // Indicate selected row and column by the current move
	  private int rowSel;
	  private int colSel;	  
	  
	@Override
	public void start(Stage primaryStage) {
		
		//declare all buttons
		Button col1 = new Button("Col 1");
		Button col2 = new Button("Col 2");
		Button col3 = new Button("Col 3");
		Button col4 = new Button("Col 4");
		Button col5 = new Button("Col 5");
		Button col6 = new Button("Col 6");
		Button col7 = new Button("Col 7");
		
		// declare class to handle button presses
		class MakeMove implements EventHandler<ActionEvent>{

	        
			@Override
			public void handle(ActionEvent event) {
				if(event.getSource().equals(col1)) {
					colSel = 1;
					waiting = false;
				}
				if(event.getSource().equals(col2)) {
					colSel = 2;
					waiting = false;
				}
				if(event.getSource().equals(col3)) {
					colSel = 3;
					waiting = false;
				}
				if(event.getSource().equals(col4)) {
					colSel = 4;
					waiting = false;
				}
				if(event.getSource().equals(col5)) {
					colSel = 5;
					waiting = false;
				}
				if(event.getSource().equals(col6)) {
					colSel = 6;
					waiting = false;
				}
				if(event.getSource().equals(col7)) {
					colSel = 7;
					waiting = false;
				}
				
			}
			
		}
		
		// Set action handlers for all buttons
		col1.setOnAction(new MakeMove());
		col2.setOnAction(new MakeMove());
		col3.setOnAction(new MakeMove());
		col4.setOnAction(new MakeMove());
		col5.setOnAction(new MakeMove());
		col6.setOnAction(new MakeMove());
		col7.setOnAction(new MakeMove());
		
		HBox buttons = new HBox();
		
		buttons.getChildren().add(col1);
		buttons.getChildren().add(col2);
		buttons.getChildren().add(col3);
		buttons.getChildren().add(col4);
		buttons.getChildren().add(col5);
		buttons.getChildren().add(col6);
		buttons.getChildren().add(col7);
		
		GridPane pane = new GridPane();
//		gameBoard.setVgap(10);
//		gameBoard.setHgap(10);
		for (int i = 0; i < 6; i++){
		    for (int k = 0; k < 7; k++){
		    	pane.add(cell[k][i] = new Cell(), k,i);
		    }
		}
		
		
		VBox layout = new VBox();
		layout.getChildren().add(lblStatus);
		layout.getChildren().add(buttons);
		layout.getChildren().add(pane);
		
		 
		Scene scene = new Scene(layout, 500, 500);
		primaryStage.setTitle("Connect 4 Online Game");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
	    // Connect to the server
	    connectToServer();
	}
	
	private void connectToServer() {
		try {
			// Create a socket to connect to the server
	        Socket socket = new Socket(host, 8000);

	        // Create an input stream to receive data from the server
	        fromServer = new DataInputStream(socket.getInputStream());

	        // Create an output stream to send data to the server
	        toServer = new DataOutputStream(socket.getOutputStream());
	      }
	      catch (Exception ex) {
	        ex.printStackTrace();
	      }

	    // Control the game on a separate thread
	    new Thread(() -> {
	      try {
	        // Get notification from the server
	        int player = fromServer.readInt();
	  
	        // Am I player 1 or 2?
	        if (player == PLAYER1) {
	          myToken = 'X';
	          otherToken = 'O';
	          Platform.runLater(() -> {
	            lblStatus.setText("Waiting for player 2 to join");
	          });
	  
	          // Receive startup notification from the server
	          fromServer.readInt(); // Whatever read is ignored
	  
	          // The other player has joined
	          Platform.runLater(() -> 
	            lblStatus.setText("Player 2 has joined. I start first"));
	  
	          // It is my turn
	          myTurn = true;
	        }
	        else if (player == PLAYER2) {
	          myToken = 'O';
	          otherToken = 'X';
	          Platform.runLater(() -> {
	            lblStatus.setText("Waiting for player 1 to move");
	          });
	        }
	  
	        // Continue to play
	        while (continueToPlay) {      
	          if (player == PLAYER1) {
	            while(myTurn) { // while move is not valid
		            waitForMove(); // Wait for player 1 to move
		            sendMove(); // Send player 1's move to the server
		            verifyMove();
	            }
	            receiveInfoFromServer(); // Receive info from the server
	          }
	          else if (player == PLAYER2) {
	            receiveInfoFromServer(); // Receive info from the server
	            while(myTurn) {
		            waitForMove(); // Wait for player 2 to move
		            sendMove(); // Send player 2's move to the server
		            verifyMove();
	            }
	          }
	        }
	      }
	      catch (Exception ex) {
	        ex.printStackTrace();
	      }
	    }).start();		
	}
	
	  /** Receive info from the server */
	  private void receiveInfoFromServer() throws IOException {
	    // Receive game status
	    int status = fromServer.readInt();

	    if (status == PLAYER1_WON) {
	      // Player 1 won, stop playing
	      continueToPlay = false;
	      if (myToken == 'X') {
	        Platform.runLater(() -> lblStatus.setText("I won! (X)"));
	      }
	      else if (myToken == 'O') {
	        Platform.runLater(() -> 
	          lblStatus.setText("Player 1 (X) has won!"));
	        receiveMove();
	      }
	    }
	    else if (status == PLAYER2_WON) {
	      // Player 2 won, stop playing
	      continueToPlay = false;
	      if (myToken == 'O') {
	        Platform.runLater(() -> lblStatus.setText("I won! (O)"));
	      }
	      else if (myToken == 'X') {
	        Platform.runLater(() -> 
	          lblStatus.setText("Player 2 (O) has won!"));
	        receiveMove();
	      }
	    }
	    else if (status == TIE) {
	      // No winner, game is over
	      continueToPlay = false;
	      Platform.runLater(() -> 
	        lblStatus.setText("Game is over, no winner!"));

	      if (myToken == 'O') {
	        receiveMove();
	      }
	    }
	    else {
	      receiveMove();
	      Platform.runLater(() -> lblStatus.setText("My turn"));
	      myTurn = true; // It is my turn
	    }
	  }	
	
	  private void receiveMove() throws IOException {
		    // Get the other player's move
		    int col = fromServer.readInt();
		    System.out.println("recieveMove(): "+ col);
		    int row = fromServer.readInt();
		    Platform.runLater(() -> cell[col][row].setToken(otherToken));
		  }

	
	  /** Wait for the player to make a move */
	  private void waitForMove() throws InterruptedException {
	    while (waiting) {
	      Thread.sleep(100);
	    }

	    waiting = true;
	  }
	  
	  private void sendMove() throws IOException {
		  toServer.writeInt(colSel); // Send the selected col
	  }
	
	  private void verifyMove() throws IOException {
		  int valid = fromServer.readInt();
		  if(valid == VALID) {
			  rowSel = fromServer.readInt();
			  Platform.runLater(() -> cell[colSel-1][rowSel].setToken(myToken));
			  myTurn = false;
			  Platform.runLater(() -> lblStatus.setText("Waiting for the other player to move"));
		  }else {
			  myTurn = true;
			  waiting = true;
		  }
	  }
	   
	  
	  public class Cell extends Pane {

		    // Token used for this cell
		    private char token = ' ';

		    public Cell() {
		      this.setPrefSize(2000, 2000); // What happens without this?
		      setStyle("-fx-border-color: black"); // Set cell's border 
		    }

		    /** Return token */
		    public char getToken() {
		      return token;
		    }

		    /** Set a new token */
		    public void setToken(char c) {
		      token = c;
		      repaint();
		    }

		    protected void repaint() {
		      if (token == 'X') {
		        Line line1 = new Line(10, 10, 
		          this.getWidth() - 10, this.getHeight() - 10);
		        line1.endXProperty().bind(this.widthProperty().subtract(10));
		        line1.endYProperty().bind(this.heightProperty().subtract(10));
		        Line line2 = new Line(10, this.getHeight() - 10, 
		          this.getWidth() - 10, 10);
		        line2.startYProperty().bind(
		          this.heightProperty().subtract(10));
		        line2.endXProperty().bind(this.widthProperty().subtract(10));
		        
		        // Add the lines to the pane
		        this.getChildren().addAll(line1, line2); 
		      }
		      else if (token == 'O') {
		        Ellipse ellipse = new Ellipse(this.getWidth() / 2, 
		          this.getHeight() / 2, this.getWidth() / 2 - 10, 
		          this.getHeight() / 2 - 10);
		        ellipse.centerXProperty().bind(
		          this.widthProperty().divide(2));
		        ellipse.centerYProperty().bind(
		            this.heightProperty().divide(2));
		        ellipse.radiusXProperty().bind(
		            this.widthProperty().divide(2).subtract(10));        
		        ellipse.radiusYProperty().bind(
		            this.heightProperty().divide(2).subtract(10));   
		        ellipse.setStroke(Color.BLACK);
		        ellipse.setFill(Color.WHITE);
		        
		        getChildren().add(ellipse); // Add the ellipse to the pane
		      }
		    }
		  }
	
	
	/**
	 * The main method is only needed for the IDE with limited
	 * JavaFX support. Not needed for running from the command line.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}

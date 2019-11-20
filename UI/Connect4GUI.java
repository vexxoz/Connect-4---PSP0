package UI;

import core.Connect4;
import core.Connect4ComputerPlayer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * GUI class for Connect 4
 * @author blake, MahmudulHassan
 *
 */
public class Connect4GUI extends Application {

	Stage myStage;
	static boolean isAiGame;
	private static Cell[][] cell = new Cell[7][6];
	/**
	 * Start of the gui
	 */
	public void start(Stage primaryStage) {
		// sets the global variable equal to our stage so we can change it later
		myStage = primaryStage;
		// create buttons
		Button ai = new Button("AI Game");
		Button player = new Button("2 Player Game");
		
		// Add handlers to buttons
		HandleAI handleAi = new HandleAI();
		ai.setOnAction(handleAi);
		HandlePlayer handlePlayer = new HandlePlayer();
		player.setOnAction(handlePlayer);
		
		// create scene layout 
		VBox layout = new VBox();
		
		// create Text output
		Text title = new Text("CONNECT 4");
		
		// create buttons layout and add buttons to it
		VBox buttons = new VBox();
		buttons.getChildren().add(ai);
		buttons.getChildren().add(player);
		
		// add groups to layout
		layout.getChildren().add(title);
		layout.getChildren().add(buttons);
		layout.setAlignment(Pos.CENTER);
		// create a scene and add it to stage
		Scene scene = new Scene(layout, 250, 250);
		myStage.setTitle("Connect 4 GUI");
		myStage.setScene(scene);
		myStage.show();
	}
	/**
	 * Class to handle the ai button press
	 * @author blake
	 *
	 */
	private class HandleAI implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent event) {
			isAiGame = true;
			gameScreen(new Connect4ComputerPlayer());
			
		}
	}
	/**
	 * Class to handle the 2 player button press
	 * @author blake
	 *
	 */
	private class HandlePlayer implements EventHandler<ActionEvent>{
		@Override
		public void handle(ActionEvent event) {
			isAiGame = false;
			gameScreen(new Connect4());
			
		}
	}
		
	/**
	 * Window for the game screen
	 * @param myGame the game object
	 */
	public void gameScreen(Connect4 myGame) {
		
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
					playMove(myGame,1);
				}
				if(event.getSource().equals(col2)) {
					playMove(myGame,2);
				}
				if(event.getSource().equals(col3)) {
					playMove(myGame,3);
				}
				if(event.getSource().equals(col4)) {
					playMove(myGame,4);
				}
				if(event.getSource().equals(col5)) {
					playMove(myGame,5);
				}
				if(event.getSource().equals(col6)) {
					playMove(myGame,6);
				}
				if(event.getSource().equals(col7)) {
					playMove(myGame,7);
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
		layout.getChildren().add(buttons);
		layout.getChildren().add(pane);
		 
		Scene scene = new Scene(layout, 500, 500);
		myStage.setTitle("Connect 4 GUI");
		myStage.setScene(scene);
		myStage.show();
		
	}

	/**
	 * Play a move
	 * @param myGame the game object
	 * @param col which col to play
	 */
	private static void playMove(Connect4 myGame, int col) {
		// returns the row of the play
		int move = myGame.playMove(col);
		if(move >= 0) {
			col--;
			cell[col][move].setToken(myGame.getTurn());
			
			if(myGame.checkWin()) {
				System.out.println(myGame.getTurn() + " Wins the game!");
				System.exit(1);
			}
			if(myGame.checkTie()) {
				System.out.println("Game is a tie!");
				System.exit(1);
			}
			// if game is an AI game
			if(isAiGame) {
				// change turn to ai 
				myGame.changeTurn();
				// run ai turn
				move = ((Connect4ComputerPlayer)myGame).calculateMove();
				
				// update the game board
				cell[((Connect4ComputerPlayer)myGame).getLastCol()][move].setToken(myGame.getTurn());
				
				// check if ai wins
				if(myGame.checkWin()) {
					System.out.println(myGame.getTurn() + " Wins the game!");
					System.exit(1);
				}
				// check if tie
				if(myGame.checkTie()) {
					System.out.println("Game is a tie!");
					System.exit(1);
				}
			}
			// change turn
			myGame.changeTurn();
			System.out.println(myGame.getTurn() + "'s Turn!");
		}else {
			System.out.println("Column Full try a different Column!");
		}
	}

	public class Cell extends Pane 
	{
	    // Token used for this cell
	    private char token = ' ';

	    public Cell() 
	    {
	 	setStyle("-fx-border-color: black");
	 	this.setPrefSize(35, 35);
	    }

	    /** Return token */
	    public char getToken() {
	 	return token;
	    }

	    /** Set a new token */
	    public void setToken(char c) 
	    {
	 	token = c;

	 	if (token == 'X') 
		{
	 	    Line line1 = new Line(10, 10, this.getWidth() - 10, this.getHeight() - 10);
		    line1.endXProperty().bind(this.widthProperty().subtract(10));
	 	    line1.endYProperty().bind(this.heightProperty().subtract(10));
		    Line line2 = new Line(10, this.getHeight() - 10, this.getWidth() - 10, 10);
		    line2.startYProperty().bind(this.heightProperty().subtract(10));
	 	    line2.endXProperty().bind(this.widthProperty().subtract(10));

	 	    // Add the lines to the pane
	 	    this.getChildren().addAll(line1, line2);
	 	}
	 	else if (token == 'O') {
	 	    Ellipse ellipse = new Ellipse(this.getWidth() / 2,
	 	    this.getHeight() / 2, this.getWidth() / 2 - 10,
	 	    this.getHeight() / 2 - 10);
	 	    ellipse.centerXProperty().bind(this.widthProperty().divide(2));
	 	    ellipse.centerYProperty().bind(this.heightProperty().divide(2));
		    ellipse.radiusXProperty().bind(this.widthProperty().divide(2).subtract(10));
	 	    ellipse.radiusYProperty().bind(this.heightProperty().divide(2).subtract(10));
		    ellipse.setStroke(Color.BLACK);
	 	    ellipse.setFill(Color.WHITE);
		    getChildren().add(ellipse); // Add the ellipse to the pane
	 	}
	    }
	 }
	
	/**
	 * Main method to start the program
	 * @param args from the command line
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
}

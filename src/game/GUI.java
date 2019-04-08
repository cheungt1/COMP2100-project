package game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static game.Util.*;

public class GUI extends Application
{
	Game game = new Game();
	
	//Creates global stages to allow only one stage to be active at once
	Stage playingStage = new Stage();
	Stage startStage = new Stage();
	
	//Public GUI Components to send to server/client
	static TextField tfUserName = new TextField();
	
	static RadioButton rbPlayer2 = new RadioButton("Player 2");
	static RadioButton rbPlayer3 = new RadioButton("Player 3");
	static RadioButton rbPlayer4 = new RadioButton("Player 4");
	
	static ToggleGroup rbPlayers = new ToggleGroup();
	
	static ComboBox<String> cbCardValues = new ComboBox<String>();
	
	//Creates Font objects to reference throughout formatting GUI components
	Font f16 = new Font("System", 16);
	Font f18 = new Font("System", 18);
	Font f20 = new Font("System", 20);
	
	//Creates card images
	ImageView imgCardBack = new ImageView();
	
	//Global player name to be used throughout various methods
	String userName = "";
	Label lblUserName = new Label(userName); //Used to display the user's name
	
	Player user = GameServer.getGame().findPlayer(userName);
	
	static int playerScore = 0;
	static Label lblPlayerScore = new Label("Your Score: " + playerScore);
	
	public static void main(String[] args) throws Exception
	{
		GameServer server = new GameServer();
		server.start();
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		//Starts the game, allowing user to input a name
		startGameGUI();
		
		//Pane initialization
		BorderPane overallPane = new BorderPane();
		StackPane pInteraction = new StackPane();
		StackPane pVisual = new StackPane();
		StackPane pTextLog = new StackPane();
		
		//Background Image initialization
		ImageView background = new ImageView();
		
		//Label initialization
		Label lblPlayerSection = new Label("Available Players");
		Label lblCardSection =  new Label("Select a Card Value");
		Label lblPlayer2Name = new Label("Player 2");
		Label lblPlayer3Name = new Label("Player 3");
		Label lblPlayer4Name = new Label("Player 4");
		Label lblRecentAction = new Label("Test Text log");
		
		//Button initialization
		Button btConfirmAction = new Button("Ask for that card");
		Button btQuit = new Button("Leave Game");
		
		//Stage modifications
		playingStage.initStyle(StageStyle.UNDECORATED);
		
		//Sets up a toggle group so only one option can be true out of the three
		rbPlayer2.setToggleGroup(rbPlayers);
		rbPlayer3.setToggleGroup(rbPlayers);
		rbPlayer4.setToggleGroup(rbPlayers);
		
		rbPlayer2.setSelected(true);
		
		//Sets up ComboBox's values
		cbCardValues.getItems().addAll("Ace", "2", "3", "4", "5", "6", "7", "8", "9", "Jack", "Queen", "King");
		
		//The following two blocks of code are from: https://stackoverflow.com/questions/45144853/javafx-combobox-displayed-item-font-size?rq=1
		cbCardValues.setCellFactory(l -> new ListCell<String>() {

	        @Override
	        protected void updateItem(String item, boolean empty) {
	        	super.updateItem(item, empty); 
	            if(empty || item==null){
	                setStyle("-fx-font-size:16");
	            } else {
	                setStyle("-fx-font-size:16");
	                setText(item.toString());
	            }
	        }

	    });
		
		cbCardValues.setButtonCell(new ListCell<String>(){

	        @Override
	        protected void updateItem(String item, boolean empty) {
	            super.updateItem(item, empty); 
	            if(empty || item==null){
	                setStyle("-fx-font-size:16");
	            } else {
	                setStyle("-fx-font-size:16");
	                setText(item.toString());
	            }
	        }

	    });
		//End of code from outside help
		
		//Combo box functionality
		cbCardValues.setValue("Ace");
		
		ImageView imgCard = new ImageView();
		ImageView imgCardBack1 = new ImageView(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\card\\b1fv.png")));
		ImageView imgCardBack2 = new ImageView(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\card\\b1fv.png")));
		ImageView imgCardBack3 = new ImageView(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\card\\b1fv.png")));
		
		
		cbCardValues.setOnAction(e -> 
		{
			try
			{
				switch(cbCardValues.getValue())
				{
					case "Ace":
						imgCard.setImage(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\card\\1.png")));
						break;
					case "Jack":
						imgCard.setImage(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\card\\11.png")));
						break;
					case "Queen":
						imgCard.setImage(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\card\\12.png")));
						break;
					case "King":
						imgCard.setImage(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\card\\13.png")));
						break;
					default:
						imgCard.setImage(new Image(new FileInputStream(System.getProperty("user.home") + 
								"\\git\\Go-Fish\\card\\" + cbCardValues.getValue() + ".png")));
						break;
				}
			}
			catch(Exception ex)
			{
				System.out.print("Image not Found");
			}
		});
		
		//Sets default image
		imgCard.setImage(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\card\\1.png")));
		
		//Setting up btQuit functionality, ending game
		btQuit.setOnAction(e -> 
		{
			//Create temporary stage
			Stage confirmStage = new Stage();
			
			//Create a StackPane for the temporary stage
			StackPane pConfirm = new StackPane();
			
			//Create a buttons for user decision
			Button btYes = new Button("Yes");
			Button btNo = new Button("No");
			
			//Creates a label to prompt a decision from user
			Label lblConfirm = new Label("Do you want to leave this game?");
			
			//Stage modifications
			confirmStage.initStyle(StageStyle.UNDECORATED);
			
			//Sets font size for all components
			lblConfirm.setFont(f18);
			btYes.setFont(f16);
			btNo.setFont(f16);
			
			btYes.setDefaultButton(true);
			
			//Creates actions for the buttons
			btYes.setOnAction(f ->
			{
				confirmStage.close();
				playingStage.close();
			});
			
			btNo.setOnAction(f ->
			{
				confirmStage.close();
			});
			
			//Adds all components onto the pane, pConfirm
			pConfirm.getChildren().addAll(lblConfirm, btYes, btNo);
			
			//Translates the components
			pConfirm.setAlignment(Pos.CENTER);
			translate(-100, 32, btYes);
			translate(100, 32, btNo);
			translate(0, -32, lblConfirm);
			
			//Size modifications to buttons
			btYes.setPrefWidth(100);
			btNo.setPrefWidth(100);
			
			
			//Creates a scene for the stage, confirmStage, and show it
			Scene confirmScene = new Scene(pConfirm, 384, 192);
			confirmStage.setScene(confirmScene);
			confirmStage.setTitle("Are you sure you wanna quit?");
			confirmStage.show();
			
		});
		
		btConfirmAction.setOnAction(e -> 
		{
			for(int i = 0; i < 4; i++)
			{
				for(int j = 0; j < 5; j++)
				{
					pVisual.getChildren().remove(8);
				}
			}
			
			
			//Update User's hand
			for(int i = 1; i < 6; i++)
			{
				ImageView userCard = new ImageView();
				
				try
				{
					userCard.setImage(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\card\\" + i + ".png")));
				} 
				catch (Exception ex)
				{
					System.out.print("Image not Found");
				}
				
				pVisual.getChildren().add(userCard);
				
				translate(13 * i - 55, 125, userCard);
			}
		});
		
		
		//Setting font sizes
		lblPlayerSection.setFont(f20);
		lblCardSection.setFont(f20);
		lblPlayerScore.setFont(f18);
		rbPlayer2.setFont(f18);
		rbPlayer3.setFont(f18);
		rbPlayer4.setFont(f18);
		btConfirmAction.setFont(f18);
		btQuit.setFont(f18);
		lblRecentAction.setFont(f16);
		lblUserName.setFont(f16);
		lblPlayer2Name.setFont(f16);
		lblPlayer3Name.setFont(f16);
		lblPlayer4Name.setFont(f16);
		
		//Adding all components into panes
		pInteraction.getChildren().addAll(lblPlayerScore, lblPlayerSection, lblCardSection, imgCard, rbPlayer2, rbPlayer3, rbPlayer4, cbCardValues, btConfirmAction, btQuit);
		pVisual.getChildren().addAll(background, imgCardBack1, imgCardBack2, imgCardBack3, lblUserName, lblPlayer2Name, lblPlayer3Name, lblPlayer4Name);
		pTextLog.getChildren().addAll(lblRecentAction);
		
		overallPane.setTop(pTextLog);
		overallPane.setCenter(pVisual);
		overallPane.setRight(pInteraction);
		
		//overallPane background color change
		overallPane.setBackground(new Background(new BackgroundFill(Color.rgb(243, 229, 192), CornerRadii.EMPTY, Insets.EMPTY))); 
		
		//pInteraction Alignment	
		translate(-20, -230, lblPlayerSection);
		translate(-20, -185, rbPlayer2);
		translate(-20, -155, rbPlayer3);
		translate(-20, -125, rbPlayer4);
		translate(-20, -75, lblCardSection);
		translate(-20, -40, cbCardValues);
		translate(-20, 35, imgCard);
		translate(-20, 110, btConfirmAction);
		translate(-20, 160, btQuit);
		translate(-20, 210, lblPlayerScore);
		
		//pVisual Alignment
		StackPane.setAlignment(lblUserName, Pos.BOTTOM_CENTER);
		translate(-30, -40, lblUserName);
		lblPlayer3Name.setRotate(180);
		translate(-30, -190, lblPlayer3Name);
		lblPlayer4Name.setRotate(270);
		translate(325, 0, lblPlayer4Name);
		lblPlayer2Name.setRotate(90);
		translate(-375.5, 0, lblPlayer2Name);
		
		translate(-27, 0, imgCardBack1);
		translate(-25, -2, imgCardBack2);
		translate(-23, -4, imgCardBack3);
		
		//Label actions set-up
		//Click and hold on the label to re-orient the label to read the player's name
		lblPlayer3Name.setOnMousePressed(e -> 
		{
			lblPlayer3Name.setRotate(0);
		});
		lblPlayer3Name.setOnMouseReleased(e -> 
		{
			lblPlayer3Name.setRotate(180);
		});
		
		lblPlayer4Name.setOnMousePressed(e -> 
		{
			lblPlayer4Name.setRotate(0);
		});
		lblPlayer4Name.setOnMouseReleased(e -> 
		{
			lblPlayer4Name.setRotate(270);
		});
		
		lblPlayer2Name.setOnMousePressed(e -> 
		{
			lblPlayer2Name.setRotate(0);
		});
		lblPlayer2Name.setOnMouseReleased(e -> 
		{
			lblPlayer2Name.setRotate(90);
		});
		
		//pVisual Card setup
		for(int i = 1; i < 6; i++)
		{
			ImageView userCard = new ImageView();
			
			try
			{
				userCard.setImage(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\card\\" + i + ".png")));
			} 
			catch (Exception ex)
			{
				System.out.print("Image not Found");
			}
			
			pVisual.getChildren().add(userCard);
			
			translate(13 * i - 55, 125, userCard);
		}
		
		//pVisual Opponent Card setup
		for(int i = 1; i < 4; i++)
		{
			for(int j = 1; j < 6; j++)
			{
				ImageView userCard = new ImageView();
			
				if(i == 1)
				{
					try
					{
						userCard.setImage(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\card\\b1fh.png")));
					}	 
					catch (Exception ex)
					{
						System.out.print("Image not Found");
					}
					
					pVisual.getChildren().add(userCard);
					
					translate(-300, 13 * j - 50, userCard);
				}
				else if(i == 2)
				{
					try
					{
						userCard.setImage(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\card\\b1fv.png")));
					}	 
					catch (Exception ex)
					{
						System.out.print("Image not Found");
					}
					
					pVisual.getChildren().add(userCard);
					
					translate(13 * j - 55, -125, userCard);
				}
				else
				{
					try
					{
						userCard.setImage(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\card\\b1fh.png")));
					}	 
					catch (Exception ex)
					{
						System.out.print("Image not Found");
					}
					
					pVisual.getChildren().add(userCard);
					
					translate(250, 13 * j - 50, userCard);
				}
			}
		}
		
		//pVisual background set-up
		background.setImage(new Image(new FileInputStream(System.getProperty("user.home") + "\\git\\Go-Fish\\GUIGraphic\\tableTexture.jpg")));
		translate(-20, 0, background);
		
		//pVisual text background set-up (Used to see text / testing)
		lblUserName.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		lblPlayer2Name.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		lblPlayer3Name.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		lblPlayer4Name.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		
		//pTextLog alignment
		translate(-465, 0, lblRecentAction);
		
		//Create Scene and set-up stage
		Scene scene = new Scene(overallPane, 1024, 532);
		playingStage.setScene(scene);
		playingStage.setTitle("Go Fish!");
		
	}
	
	//Creates the first box that a player would see
	public void startGameGUI()
	{
		//Creates a temporary StackPane
		StackPane startPane = new StackPane();
		
		//Creates components
		Label lblMessage1 = new Label("Welcome!");
		Label lblMessage2 = new Label("Please Enter Your Name!");
		
		Button btConfirm = new Button("Play the Game!");
		
		//Stage modifications
		startStage.initStyle(StageStyle.UNDECORATED);
		
		//Setting font sizes
		lblMessage1.setFont(f18);
		lblMessage2.setFont(f18);
		tfUserName.setFont(f16);
		btConfirm.setFont(f18);
		
		//Shrinking the text field's width
		tfUserName.setMaxWidth(192);
		
		//Setting up btConfirm functionality (Read from tfPlayerName and set the string playerName to that)
		btConfirm.setDefaultButton(true);
		
		//Adds all components into the stack pane
		startPane.getChildren().addAll(lblMessage1, lblMessage2, tfUserName, btConfirm);
		
		//Translating all components
		translate(0, -48, lblMessage1);
		translate(0, -24, lblMessage2);
		translate(0, 16, tfUserName);
		translate(0, 64, btConfirm);
		
		
		Scene startScene = new Scene(startPane, 384, 192);
		startStage.setScene(startScene);
		startStage.setTitle("Welcome Player!");
		startStage.show();
		
		try
		{
			Socket socket = new Socket("localhost", 8000);
			
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			DataInputStream is = new DataInputStream(socket.getInputStream());

			lblMessage2.setText(is.readUTF());
			
			btConfirm.setOnAction(e ->
			{
				//Checks if the user entered a valid name or not
				if(tfUserName.getText().compareTo("") != 0)
				{
					//Sets the player name to what was entered
					writeString(os, tfUserName.getText());
					updateUserName(tfUserName.getText());
					
					//Closes this stage and shows the stage for the actual game
					startStage.close();
					playingStage.show();
				}
				else
				{
					//Changes label to warn user of entering a name
					lblMessage1.setVisible(false);
					lblMessage2.setText("You MUST enter a name!");
				}
			});
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		
	}
	
	//Created to translate a GUI component in the x and y axis at the same time
	public void translate(double x, double y, Node node)
	{
		node.setTranslateX(x);
		node.setTranslateY(y);
	}
	
	//Updates the String, userName, and the label, lblUserName
	public void updateUserName(String newName)
	{
		userName = newName;
		lblUserName.setText(newName);
	}
	
	//The following methods allows client/server interaction
	public static String getUserName()
	{
		return tfUserName.getText();
	}
	
	public static String getCardValue()
	{
		return cbCardValues.getValue();
	}
	
	public static String getPlayerChoice()
	{
		if(rbPlayer2.isSelected())
		{
			return rbPlayer2.getText();
		}
		else if(rbPlayer3.isSelected()) 
		{
			return rbPlayer3.getText();
		}
		else
		{
			return rbPlayer4.getText();
		}
	}
	
	public static void updateScore()
	{
		if(playerScore < 13)
		{
			playerScore++;
			updateLabelScore();
		}
	}
	
	private static void updateLabelScore()
	{
		lblPlayerScore.setText("Your Score: " + playerScore);
	}
	
	
}
/* Name: Liseidy Bueno
 * Program Status: Complete
 * Description: This program connects to a server and allows the client to read messages input
 * by the server and to send messages to the server using the ENTER key to send.
 */

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ChatServer extends Application{
	
	//create input and output streams
	private DataInputStream fromClient;
	private DataOutputStream toClient;
	
	//create socket
	private Socket socket;
	
	//text area for displaying server text
	private TextField servertextarea = new TextField();
	
	//text area for displaying client text 
	private TextArea clienttextarea = new TextArea();
	
	@Override
	public void start(Stage primarystage) throws IOException {
		
		//create vbox for text areas 
		VBox serverbox = new VBox();
		
		//create labels for server & client
		Label lblserver = new Label("Server");
		Label lblclient = new Label("Client");
		
		//set alignment for server text field so text is input on top left 
		servertextarea.setAlignment(Pos.TOP_LEFT);
		
		//set size of server text field
		servertextarea.setPrefSize(300, 140);
		
		//set height for client text area and set to not editable 
		clienttextarea.setPrefHeight(140);
		clienttextarea.setEditable(false);
		
		//create scrollpanes for each textarea
		ScrollPane serverscroll = new ScrollPane();
		ScrollPane clientscroll = new ScrollPane();
		
		//set text areas in scrollpanes
		serverscroll.setContent(servertextarea);
		clientscroll.setContent(clienttextarea);
		
		//put nodes into vobx
		serverbox.getChildren().addAll(lblclient, clientscroll, lblserver, serverscroll);
		serverbox.setPrefWidth(300);
		
		//create scene
		Scene scene = new Scene(serverbox);
		
		primarystage.setScene(scene);
		primarystage.show();
		
		
		new Thread( () -> {
			try {
			//create server socket and socket
			@SuppressWarnings("resource")
			ServerSocket serversocket = new ServerSocket(9000);
			socket = serversocket.accept();
			
			// Initialize data input and output streams
	        fromClient = new DataInputStream(socket.getInputStream());
	        toClient = new DataOutputStream(socket.getOutputStream());
			
			Platform.runLater( () -> {
		    // Display the client number
		         clienttextarea.appendText("Starting thread for client at " + new Date() + '\n');
			});
			
	        
	        while(true) {
	        	//read text from client
	        	String textfromclient = fromClient.readUTF();
	        	
	        	//show text on screen
	        	Platform.runLater( () -> {
	        		clienttextarea.appendText("Client: " + textfromclient + "\n");
	        	});
	        		
	        	//send message
				servertextarea.setOnAction(e -> {
					try {
						//get text from text area 
						String servertext = servertextarea.getText();
						//write on the output stream and clear text field
						toClient.writeUTF(servertext);
						clienttextarea.appendText("Server: " + servertext + "\n");
						servertextarea.clear();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				});
	        	
	        }
	        } catch (IOException ex) {
				System.err.println(ex);
			} 
			//close datastreams
			try {
				toClient.close();
				fromClient.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			//start thread
		}).start();
		
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}

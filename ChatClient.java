/* Name: Liseidy Bueno
 * Program Status: Complete
 * Description: This program connects to a server and allows the client to read messages inputted 
 * by the server and to send messages to the server using the ENTER key to send.
 */

import java.io.*;
import java.net.*;
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


public class ChatClient extends Application{
	
	
	//text area for displaying server text
	private TextField clienttextarea = new TextField();
	//text area for displaying client text 
	private TextArea servertextarea = new TextArea();
		
		
	@Override
	public void start(Stage primarystage) throws UnknownHostException, IOException {
			
		//create vbox for text areas 
		VBox serverbox = new VBox();
			
		//create labels for server & client
		Label lblserver = new Label("Server");
		Label lblclient = new Label("Client");
			
		//set alignment for client text field so text is input on top left 
		clienttextarea.setAlignment(Pos.TOP_LEFT);
		
		//set size for client text field
		clienttextarea.setPrefSize(300, 140);
		
		//set height for server textarea and set to not editable
		servertextarea.setPrefHeight(140);
		servertextarea.setEditable(false);
			
		//create scrollpanes for each textarea
		ScrollPane serverscroll = new ScrollPane();
		ScrollPane clientscroll = new ScrollPane();
	
		//set text areas in scrollpanes
		serverscroll.setContent(servertextarea);
		clientscroll.setContent(clienttextarea);
			
		//put nodes into vobx
		serverbox.getChildren().addAll(lblserver, serverscroll, lblclient, clientscroll);
		serverbox.setPrefWidth(300);
			
		//create scene
		Scene scene = new Scene(serverbox);
			
		//set scene in stage and show stage
		primarystage.setScene(scene);
		primarystage.show();
			
		//establish connection
		@SuppressWarnings("resource")
		Socket socket = new Socket("localhost", 9000);
			
		// create and initialize IO streams
		DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
		DataInputStream fromServer = new DataInputStream(socket.getInputStream());
			
		//send message
		clienttextarea.setOnAction(e -> {
			try {
				//get text from text area 
				String clienttext = clienttextarea.getText();
				//write on the output stream
				toServer.writeUTF(clienttext);
				servertextarea.appendText("Client: " + clienttext + "\n");
				clienttextarea.clear();
			} catch (IOException ex) {
				ex.printStackTrace();
			} 					
		});
					
		//read message thread
		Thread readmsg = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						//read the message send to client
						String readmessage = fromServer.readUTF();
						
						Platform.runLater(() -> {
			        		servertextarea.appendText("Server: " + readmessage + "\n");
			        	});
						
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		
		readmsg.start();
			
	}

	public static void main(String[] args) {
		launch(args);
	}
		

}

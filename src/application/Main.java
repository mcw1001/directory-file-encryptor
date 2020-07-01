package application;
	
//import javafx.application.Application;
import javafx.stage.Stage;
import application.CryptoGen;
import application.GenException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;

/**
 * @author Michael
 *
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			//BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Encryptor.fxml"));
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/Encryptor.fxml"));
			Pane root = (Pane) fxmlLoader.load();	//FXMLLoader.load(getClass().getResource("Encryptor.fxml"));
			((EncryptorController) fxmlLoader.getController()).setStage(primaryStage);
			Scene scene = new Scene(root,600,440);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
//						Parent root = (Parent) fxmlLoader.load();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);

		
	}//*/
	
	
}

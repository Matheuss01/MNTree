package BPlusTreeGUI;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	
	@Override
	public void start(Stage stage) {
		try {
			URL url = getClass().getResource("/BPlusTreeGUI/MainFrame.fxml");
			FXMLLoader loader = new FXMLLoader(url);
			BorderPane root = (BorderPane)loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("B+ Tree");
			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}
}

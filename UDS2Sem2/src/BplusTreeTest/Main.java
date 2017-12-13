package BplusTreeTest;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	MainFrameController mfc;
	@Override
	public void start(Stage stage) {
		try {
			URL url = getClass().getResource("/BplusTreeTest/MainFrame.fxml");
			FXMLLoader loader = new FXMLLoader(url);
			BorderPane root = (BorderPane)loader.load();
			mfc=((MainFrameController)loader.getController());
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("B+ Tree");
			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() throws IOException{
	    System.out.println("Stage is closing");
	    //System.out.println(mfc==null);
	    mfc.closeRAF();
	}
	
	
	
	public static void main(String[] args) throws Exception {
		launch(args);
	}
}

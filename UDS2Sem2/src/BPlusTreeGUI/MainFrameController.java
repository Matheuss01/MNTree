package BPlusTreeGUI;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import BPlusTree.BPlusTree;
import Classes.Numb;
import Comparators.Comparators;
import Converters.NumbConverter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class MainFrameController implements Initializable {
	@FXML private Button showBlocksButton;
	
	private BPlusTree<Numb> bPlusTree ; //toto sa potom poriesi tak ze sa najprv zvoli ci sa chce strom nacitat zo suboru alebo vytvorit novy a podla toho sa strom inicializuje
	private AllBlocksFrameController allBlocksController;
	
	@FXML
	public void showBlocks() throws Exception {
		//loading new stage
		URL url = getClass().getResource("/BPlusTreeGUI/AllBlocksFrame.fxml");
		FXMLLoader loader = new FXMLLoader(url);
		ScrollPane root = (ScrollPane)loader.load();
		allBlocksController=loader.getController();
		
		for (int i = 0; i < 9; i++) {
			allBlocksController.addBlock(bPlusTree.getDiskManager().readBlock(i));
		}
				
		Stage stage = new Stage();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("File Blocks");
		stage.show();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) { //toto tu pre strom neskor nebude
		try {
			//loading b+ tree
			bPlusTree= new BPlusTree<Numb>(3, Comparators.numbComparator, new NumbConverter(Comparators.numbComparator), 100, 4, 14);
			Numb[] numbs = new Numb[15];
			numbs[0] = new Numb(0,"Zero");
			numbs[1]= new Numb(1,"One");
			numbs[2] = new Numb(2,"Two");
			numbs[3] = new Numb(3,"Three");
			numbs[4] = new Numb(4,"Four");
			numbs[5] = new Numb(5,"Five");
			numbs[6] = new Numb(6,"Six");
			numbs[7] = new Numb(7,"Seven");
			numbs[8] = new Numb(8,"Eight");
			numbs[9] = new Numb(9,"Nine");
			numbs[10] = new Numb(10,"Ten");
			numbs[11] = new Numb(11,"Eleven");
			numbs[12] = new Numb(12,"Twelve");
			numbs[13] = new Numb(13,"Thirteen");
			numbs[14] = new Numb(14,"Fourteen");
			for (int i = 0; i < 10; i++) {
				bPlusTree.add(numbs[i]);
			}
			/*Random rnd = new Random();
			for (int i = 0; i < 4; i++) {
				bPlusTree.add(numbs[i]);
			}*/
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		
	}

}

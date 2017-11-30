package BPlusTreeGUI;

import BPlusTree.Block;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;

public class AllBlocksFrameController {
	
	@FXML private HBox hBox;
	private int numOfBlocks;
	
	public void addBlock(Block block) {
		TextArea blockText = new TextArea();
		String s="Block # "+numOfBlocks+++"\n\n";
		s+=block.toString();
		blockText.setText(s);
		blockText.setMinWidth(350);
		hBox.getChildren().add(blockText);
	}
	
	
}

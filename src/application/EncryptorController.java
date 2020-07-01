package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
//import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public class EncryptorController implements Initializable{
	//@FXML
	
	private File dir;//global directory/folder File variable (for full directories)

	private List<File> fileList;
	private List<File> fileListTemp; //area to hold previous file list after encrypting with KEEPOLD
	
	@FXML
	private Button btnFileChooser;
	@FXML
	private Label labelFChooser;
	
	@FXML
	private Button btnDirChooser;
	@FXML
	private Label labelDChooser;
	
	@FXML
	private Button btnENCRYPT;
	@FXML
	private Button btnDECRYPT;
	
	@FXML
	private TextArea txtArea;
	@FXML
	private TextArea fileArea;
	@FXML
	private TextField keyField;
	
	private Boolean KEEPOLD=false; //starts false radio button deselected by default
	@FXML
	private RadioButton keepOldBtn;
	
	
	private Boolean ENC_NAMES=true;
	@FXML
	private RadioButton encNamesBtn;
	
	@FXML
	Pane p; //this id is given to the pane, allowing the stage to be referenced in functions

	private Stage stage;
	
	
	/**
	 * NOTES for further improvement
	 * 	use internal vars to hold file information instead of reading it from the textboxes and textareas
	 * 	
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//keepOldBtn.fire(); // program starts with keepOld ON
		encNamesBtn.fire();
		//alternatively use setSelected instead of .fire()
		
	}
	
	public void chooseFile(ActionEvent e){
		dir = null; //TODO remove this and deal with !=null cases
		FileChooser fc = new FileChooser();
		//IMPORTANT, declare fileList as new ArrayList instead of using the raw List output of showOpenMult.. as it helps avoid cast issues later
		fileList = new ArrayList<File> (fc.showOpenMultipleDialog(stage));
//		fileArrList = (ArrayList<File>) fc.showOpenMultipleDialog(stage);
		if(fileList!=null) { //if user hits "cancel" then this will be null
			fileArea.setText("");//if area blank, ___?
			for(File file: fileList) {//files) {
				fileArea.appendText(file.getAbsolutePath()+"\r\n");
			}
		}
//		FileCruncher.printFilesArray(files);
	}
	public void chooseFolder(ActionEvent e) {
		DirectoryChooser dc = new DirectoryChooser();
		//Stage stg = (Stage) p.getScene().getWindow(); //nullptr exception ;(
		dir = dc.showDialog(stage);//stage is passed from main to controller w/ setStage function
		if(dir!=null) {//if user hits "cancel" then this will be null
			fileArea.setText(dir.getAbsolutePath());
			fileList = FileCruncher.unpackFolderList(dir);//new ArrayList<File> (Arrays.asList(dir.listFiles()));
		}
	}
	
	/*
	 * TODO implement threads and a progress bar so large directories don't stall the interface
	 */
	/**
	 * 
	 * @param e
	 */
	public void ENCRYPT(ActionEvent e) {
		String key = keyField.getText();
    	if(key.length()!=16) {
    		/** Ideally, this spot would inform the user with a pop up, not an 'output box', DOLATER*/
    		txtArea.appendText("\r\n"+"'"+key+"' is not 16 chars, generating new one...");
    		key = CryptoGen.stringGen(16);
    		txtArea.appendText("\r\n"+"new key: "+key+"\r\n");
    		keyField.setText(key);
    	}
    	

    	if(ENC_NAMES) {
    		File nameFile;
    		if(dir!=null) {
    			nameFile = new File(dir.getAbsolutePath()+"\\"+"Xx99A&Btg");//this will be the namefile's name
    		}else {
    			nameFile = new File(fileList.get(0).getParent()+"\\"+"Xx99A&Btg");
    		}
    		//files = FileCruncher.nameScramble(files, dir,nameFile);
    		fileList = FileCruncher.nameScramble((ArrayList<File>) fileList,nameFile);

    	}
    	//call encryption on directory
		//FileCruncher.searchEncrypt(key,files,KEEPOLD);
		FileCruncher.searchEncrypt(key,(ArrayList<File>) fileList,KEEPOLD);

		txtArea.appendText("File(s) encrypted successfully.");
	}
	
	public void DECRYPT(ActionEvent e) {
		String key = keyField.getText();
    	if(key.length()!=16) {
    		/** Ideally, this spot would inform the user with a pop up, not with a textarea, DOLATER*/
    		txtArea.setText("'"+key+"' is not 16 chars, cannot decrypt!"+"\r\n");
    		return;
    	}
    	if(KEEPOLD) {
//    		fileList = new ArrayList<File> (Arrays.asList(dir.listFiles(new encFilter())));//filter to only decrypt .enc files
    		fileListTemp = fileList;
    		fileList = FileCruncher.getEncs((ArrayList<File>) fileList);
    	}
    	
		FileCruncher.searchDecrypt(key,(ArrayList<File>) fileList,KEEPOLD);
    	if(ENC_NAMES) {//comes after decryption so names.txt is accessible
    		File nameFile;
    		if(dir!=null) {
    			nameFile = new File(dir.getAbsolutePath()+"\\"+"Xx99A&Btg");//this will be the namefile's name
    		}else {
    			nameFile = new File(fileList.get(0).getParent()+"\\"+"Xx99A&Btg");
    		}
    		fileList = FileCruncher.nameUnscramble(nameFile);
    		//files = dir.listFiles();//call AGAIN because file names have changed 
    	}
    	if(KEEPOLD) {
    		fileList = fileListTemp;
    	}
		txtArea.setText("File(s) decrypted successfully.");
	}
	public void setKEEPOLD(ActionEvent e) {
		if(keepOldBtn.isSelected()) {
			KEEPOLD = true;
			if(ENC_NAMES == true) {encNamesBtn.fire();}//turns the other off, could incorporate into group instead?
		}else {
			KEEPOLD = false;
		}
	}
	
	public void setENC_NAMES(ActionEvent e) {
		if(encNamesBtn.isSelected()) {
			ENC_NAMES = true;
			if(KEEPOLD == true) {keepOldBtn.fire();}//turns the other off? could incorporate into group
		}else {
			ENC_NAMES = false;
		}
	}
	
	/**
	 * @param stage - a Stage object made in main, passed to an instance of this controller to allow referencing
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}
}

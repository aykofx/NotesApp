import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

public class NotesController {

	@FXML
	private TextArea input;
	
	@FXML
	private Label counter;
	
	private File currentFile;
	
	@FXML
	private void initialize() {
		counter.setText("");
		input.textProperty().addListener((obs, oldText, newText) -> {
			int chars = newText.length();
			int words = newText.trim().isEmpty() ? 0 : newText.trim().split("\\s+").length;
			counter.setText("Words: "+ words + " | Characters: "+ chars);
		});
		
	    input.setOnDragOver(e -> {
	        if (e.getDragboard().hasFiles() && e.getDragboard().getFiles().get(0).getName().endsWith(".txt")) {
	            e.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
	        }
	        e.consume();
	    });

	    input.setOnDragDropped(e -> {
	        var db = e.getDragboard();
	        if (db.hasFiles()) {
	            File file = db.getFiles().get(0);
	            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
	                input.clear();
	                br.lines().forEach(line -> input.appendText(line + "\n"));
	            } catch (IOException ex) {
					Alert a = new Alert(AlertType.ERROR);
					a.setResizable(false);
					a.setHeaderText("Important Information!");
					a.setContentText("Something went wrong!");
					a.showAndWait();
	            }
	        }
	        e.setDropCompleted(true);
	        e.consume();
	    });
	}
	
	@FXML
	private void save() {
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		fileChooser.setInitialFileName(date+"-notes.txt");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		
		File file = fileChooser.showSaveDialog(input.getScene().getWindow());
		if (file != null) {
			currentFile = file;
			try (FileWriter writer = new FileWriter(file)) {
				writer.write(input.getText());
			} catch (IOException e) {
				Alert a = new Alert(AlertType.ERROR);
				a.setResizable(false);
				a.setHeaderText("Important Information!");
				a.setContentText("Couldn't save file!");
				a.showAndWait();
			}
		}
	}
	
	@FXML
	private void quickSave() {
		if (currentFile != null) {
			try (FileWriter writer = new FileWriter(currentFile)) {
				writer.write(input.getText());
			} catch (IOException e) {
				Alert a = new Alert(AlertType.ERROR);
				a.setResizable(false);
				a.setHeaderText("Important Information!");
				a.setContentText("Couldn't save file!");
				a.showAndWait();
			}
		} else {
			save();
		}
	}
	
	@FXML
	private void load() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select ur file");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		
		File file = fileChooser.showOpenDialog(null);
		
		if (file != null) {
			currentFile = file;
			try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
				input.clear();
				String chars;
				while ((chars = br.readLine()) != null) {
					input.appendText(chars + System.lineSeparator());
				}
			} catch (IOException e) {
				Alert a = new Alert(AlertType.ERROR);
				a.setResizable(false);
				a.setHeaderText("Error");
				a.setContentText("Something went wrong there!");
				a.showAndWait();
			}
		} else {
			Alert a = new Alert(AlertType.INFORMATION);
			a.setResizable(false);
			a.setHeaderText("Important Information!");
			a.setContentText("No file selected!");
			a.showAndWait();
		}
	}
	
	@FXML
	private void end() {
		
		if (currentFile != null) {
			Alert a = new Alert(AlertType.CONFIRMATION);
			a.setResizable(false);
			a.setHeaderText("Important Information!");
			a.setContentText("Do you want to save before you exit?");
			a.showAndWait();
			
			if (a.getResult() == ButtonType.OK) {
				quickSave();
				System.exit(0);
			} else {
				System.exit(0);
			}
			
		} else {
			Alert a = new Alert(AlertType.CONFIRMATION);
			a.setResizable(false);
			a.setHeaderText("Important Information!");
			a.setContentText("You are about to exit the App!");
			a.showAndWait();
			
			if (a.getResult() == ButtonType.OK) {
				System.exit(0);
			} else {
				return;
			}	
		}
	}
	
	@FXML
	private void newText() {
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setResizable(false);
		a.setHeaderText("Important Information!");
		a.setContentText("You are about to delete your Text!");
		a.showAndWait();
		
		if (a.getResult() == ButtonType.OK) {
			input.setText("");
			currentFile = null;
		} else {
			return;
		}
	}
	
}

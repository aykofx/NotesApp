// Import necessary Java and JavaFX libraries
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

	// UI components connected to FXML
	@FXML
	private TextArea input;

	@FXML
	private Label counter;

	// Stores the file currently being edited
	private File currentFile;

	// Method that runs when the app starts
	@FXML
	private void initialize() {
		// Set counter label to empty at the beginning
		counter.setText("");

		// Add listener to update word and character count when user types
		input.textProperty().addListener((obs, oldText, newText) -> {
			int chars = newText.length();  // Count characters
			int words = newText.trim().isEmpty() ? 0 : newText.trim().split("\\s+").length;  // Count words
			counter.setText("Words: " + words + " | Characters: " + chars);
		});

		// Allow dragging .txt files into the text area
		input.setOnDragOver(e -> {
			if (e.getDragboard().hasFiles() && e.getDragboard().getFiles().get(0).getName().endsWith(".txt")) {
				e.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
			}
			e.consume();
		});

		// Load content from dragged file into the text area
		input.setOnDragDropped(e -> {
			var db = e.getDragboard();
			if (db.hasFiles()) {
				File file = db.getFiles().get(0);
				try (BufferedReader br = new BufferedReader(new FileReader(file))) {
					input.clear();  // Clear current text
					br.lines().forEach(line -> input.appendText(line + "\n"));  // Add file content
				} catch (IOException ex) {
					// Show error alert if file can't be read
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

	// Save content to a new file
	@FXML
	private void save() {
		// Get current date and time to create default file name
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		fileChooser.setInitialFileName(date + "-notes.txt");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

		// Show save dialog
		File file = fileChooser.showSaveDialog(input.getScene().getWindow());
		if (file != null) {
			currentFile = file;
			try (FileWriter writer = new FileWriter(file)) {
				writer.write(input.getText());  // Write text to file
			} catch (IOException e) {
				// Show error alert if file can't be saved
				Alert a = new Alert(AlertType.ERROR);
				a.setResizable(false);
				a.setHeaderText("Important Information!");
				a.setContentText("Couldn't save file!");
				a.showAndWait();
			}
		}
	}

	// Save content to current file quickly
	@FXML
	private void quickSave() {
		if (currentFile != null) {
			try (FileWriter writer = new FileWriter(currentFile)) {
				writer.write(input.getText());  // Write to the current file
			} catch (IOException e) {
				// Show error alert if something goes wrong
				Alert a = new Alert(AlertType.ERROR);
				a.setResizable(false);
				a.setHeaderText("Important Information!");
				a.setContentText("Couldn't save file!");
				a.showAndWait();
			}
		} else {
			// If no file selected yet, use save() instead
			save();
		}
	}

	// Load content from a selected file
	@FXML
	private void load() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select ur file");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

		File file = fileChooser.showOpenDialog(null);

		if (file != null) {
			currentFile = file;
			try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
				input.clear();  // Clear existing text
				String chars;
				while ((chars = br.readLine()) != null) {
					input.appendText(chars + System.lineSeparator());  // Add each line
				}
			} catch (IOException e) {
				// Show error alert
				Alert a = new Alert(AlertType.ERROR);
				a.setResizable(false);
				a.setHeaderText("Error");
				a.setContentText("Something went wrong there!");
				a.showAndWait();
			}
		} else {
			// If no file selected
			Alert a = new Alert(AlertType.INFORMATION);
			a.setResizable(false);
			a.setHeaderText("Important Information!");
			a.setContentText("No file selected!");
			a.showAndWait();
		}
	}

	// Close the app with a confirmation prompt
	@FXML
	private void end() {
		if (currentFile != null) {
			Alert a = new Alert(AlertType.CONFIRMATION);
			a.setResizable(false);
			a.setHeaderText("Important Information!");
			a.setContentText("Do you want to save before you exit?");
			a.showAndWait();

			if (a.getResult() == ButtonType.OK) {
				quickSave();  // Save before exit
				System.exit(0);
			} else {
				System.exit(0);  // Exit anyway
			}

		} else {
			Alert a = new Alert(AlertType.CONFIRMATION);
			a.setResizable(false);
			a.setHeaderText("Important Information!");
			a.setContentText("You are about to exit the App!");
			a.showAndWait();

			if (a.getResult() == ButtonType.OK) {
				System.exit(0);  // Exit app
			} else {
				return;  // Do nothing if canceled
			}
		}
	}

	// Start a new note (clears current text)
	@FXML
	private void newText() {
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setResizable(false);
		a.setHeaderText("Important Information!");
		a.setContentText("You are about to delete your Text!");
		a.showAndWait();

		if (a.getResult() == ButtonType.OK) {
			input.setText("");  // Clear text
			currentFile = null;  // Reset current file
		} else {
			return;  // Do nothing if canceled
		}
	}
}

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch (args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
        FXMLLoader loader = new FXMLLoader(getClass().getResource("notes.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("Notes App");
        stage.setScene(scene);
        stage.show();
	}

}

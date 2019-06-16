package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class MainApplication extends Application
{
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		primaryStage.setTitle("Flight Controller");
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(new Image(new FileInputStream("./PTM2_Interpeter/Resources/Icon.png")));
		// close all threads and exit application when the X button is clicked in the window (this is done so that any background threads that run will close)
		primaryStage.setOnCloseRequest(event-> System.exit(0));

		// load mainWindow.fxml and put it in a window(stage...)
		Parent root =  FXMLLoader.load(getClass().getResource("./MainWindow.fxml"));
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}

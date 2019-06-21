package View;

import Model.InterpreterModel;
import ViewModel.ViewModel;
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

		// set MVVM bindings
		InterpreterModel model = new InterpreterModel();
		ViewModel viewModel = new ViewModel(model);
		model.addObserver(viewModel);

		FXMLLoader fxmlLoader = new FXMLLoader();// creating an instance so we can get the controller for the fxml we will load
		Parent root = fxmlLoader.load(getClass().getResource("MainWindow.fxml").openStream());
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		/*
		// load mainWindow.fxml and put it in a window(stage...)
		Parent pathLines =  FXMLLoader.load(getClass().getResource("./MainWindow.fxml"));
		primaryStage.setScene(new Scene(pathLines));
		primaryStage.show();
		*/

		// more MVVM
		MainWindowController view = fxmlLoader.getController(); // this is the VIEW
		view.setViewModel(viewModel);
		viewModel.addObserver(view);
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}

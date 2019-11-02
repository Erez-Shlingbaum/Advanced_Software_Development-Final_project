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
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("Flight Controller");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(new FileInputStream("./PTM2_Interpeter/Resources/Icon.png")));

        // Close all threads and exit application when the X button is clicked in the window (this is done so that any background threads that run will close)
        primaryStage.setOnCloseRequest(event -> System.exit(0));

        // Set MVVM bindings
        InterpreterModel model = new InterpreterModel();
        ViewModel viewModel = new ViewModel(model);
        model.addObserver(viewModel);

        FXMLLoader fxmlLoader = new FXMLLoader(); // Creating an instance so we can get the controller for the fxml we will load
        Parent root = fxmlLoader.load(getClass().getResource("MainWindow.fxml").openStream());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // Continuation of MVVM setup
        MainWindowController view = fxmlLoader.getController(); // This is the VIEW
        view.setViewModel(viewModel);
        viewModel.addObserver(view);
    }
}

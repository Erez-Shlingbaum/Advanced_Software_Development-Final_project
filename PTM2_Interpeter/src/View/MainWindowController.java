package View;

import ViewModel.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainWindowController implements Observer, Initializable
{
    @FXML
    RadioButton autoPilotRadioButton;
    BooleanProperty isAutoPilotMode; // if true the autopilot mode, else - manual mode

    ViewModel viewModel;

    //FXML members
    @FXML
    MapDisplayer mapDisplayer;
    @FXML
    TextArea autoPilotScriptTextArea;
    @FXML
    JoystickControl joystickController;
    //property for the binding
    StringProperty pathCalculatorServerIP;
    StringProperty pathCalculatorServerPORT;

    // csv
    StringProperty csvFilePath;
    StringProperty simulatorIP;
    StringProperty simulatorPort;

    //constructor
    public MainWindowController()
    {
        // autopilot
        isAutoPilotMode = new SimpleBooleanProperty();

        // csv
        csvFilePath = new SimpleStringProperty();

        // path solving
        pathCalculatorServerIP = new SimpleStringProperty();
        pathCalculatorServerPORT = new SimpleStringProperty();

        // connection to simulator
        simulatorIP = new SimpleStringProperty();
        simulatorPort = new SimpleStringProperty();
    }


    void setViewModel(ViewModel viewModel)
    {
        this.viewModel = viewModel;

        // auto pilot
        viewModel.scriptToInterpret.bind(autoPilotScriptTextArea.textProperty());

        // csv
        viewModel.csvFilePath.bind(csvFilePath);
        mapDisplayer.xCoordinateLongitude.bind(viewModel.xCoordinateLongitude);
        mapDisplayer.yCoordinateLatitude.bind(viewModel.yCoordinateLatitude);
        mapDisplayer.cellSizeInDegrees.bind(viewModel.cellSizeInDegrees);
        mapDisplayer.mapData.bind(viewModel.heightsInMetersMatrix);

        // path solving
        viewModel.pathCalculatorServerIP.bind(pathCalculatorServerIP);
        viewModel.pathCalculatorServerPORT.bind(pathCalculatorServerPORT);

        viewModel.xStartIndex.bind(mapDisplayer.planeIndexX);
        viewModel.yStartIndex.bind(mapDisplayer.planeIndexY);
        viewModel.xEndIndex.bind(mapDisplayer.xEndIndex);
        viewModel.yEndIndex.bind(mapDisplayer.yEndIndex);

        // connection to simulator
        viewModel.simulatorIP.bind(simulatorIP);
        viewModel.simulatorPort.bind(simulatorPort);

        // map displayer
        mapDisplayer.pathToEndCoordinate.bind(viewModel.pathToEndCoordinate); // getting solution from ptm1 server

        // map displayer - current plane position
        mapDisplayer.currentPlaneLongitudeX.bind(viewModel.currentPlaneLongitudeX);
        mapDisplayer.currentPlaneLatitudeY.bind(viewModel.currentPlaneLatitudeY);

        // joystick
		/*viewModel.isAutoPilotMode.bind(isAutoPilotMode);
		viewModel.xAxisJoystick.bind(joystickController.xAxisJoystick);
		viewModel.yAxisJoystick.bind(joystickController.yAxisJoystick);
		viewModel.rudderJoystick.bind(joystickController.downSlider.valueProperty());
		viewModel.throttleJoystick.bind(joystickController.leftSlider.valueProperty()); */

        viewModel.isAutoPilotMode.bindBidirectional(isAutoPilotMode);
        viewModel.xAxisJoystick.bindBidirectional(joystickController.xAxisJoystick);
        viewModel.yAxisJoystick.bindBidirectional(joystickController.yAxisJoystick);
        viewModel.rudderJoystick.bindBidirectional(joystickController.downSlider.valueProperty());
        viewModel.throttleJoystick.bindBidirectional(joystickController.leftSlider.valueProperty());

		// update map
		viewModel.asyncMapPlanePositionUpdater();
    }

    public void onConnectToSimulator(ActionEvent actionEvent)
    {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Connect");
        dialog.setHeaderText("Connect to simulator");

        // Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("airplaneConnect.png").toString()));	// TODO :)

        // Set the button types.
        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ip = new TextField();
        ip.setPromptText("IP Address");
        TextField port = new TextField();
        port.setPromptText("Port");

        grid.add(new Label("IP:"), 0, 0);
        grid.add(ip, 1, 0);
        grid.add(new Label("Port:"), 0, 1);
        grid.add(port, 1, 1);

        // set the grid a child of the dialog
        dialog.getDialogPane().setContent(grid);

        // Convert the result to a ip-port-pair when the connectButton IS CLICKED
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType)
                return new Pair<>(ip.getText(), port.getText());
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        if (!result.isPresent())
            return;

        simulatorIP.set(result.get().getKey());
        simulatorPort.set(result.get().getValue());

        viewModel.connectToSimulator();
    }

    public void onOpenData(ActionEvent actionEvent)
    {

        //mapDisplayer.mapData.set(mapData);
        //mapDisplayer.redrawPath("Right,Right,Down,Down,Right,Up");

        // show dialog to open csvFile file
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("Open a csv file");
        fileDialog.setInitialDirectory(new File("."));
        fileDialog.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));

        File csvFile = fileDialog.showOpenDialog(mapDisplayer.getScene().getWindow()); // 1 way to get primary window is through an item in that window...
        if (csvFile == null)
            return; // do nothing if no file was chosen

        csvFilePath.set(csvFile.getPath());
        viewModel.openCsvFile();

        //mapDisplayer.redraw(); TODO for map displayer to draw csv

    }

    public void onCalculatePath(ActionEvent actionEvent)
    {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Connect");
        dialog.setHeaderText("Connect to path calculator server");

        // Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("airplaneConnect.png").toString()));

        // Set the button types.
        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField ip = new TextField();
        ip.setPromptText("IP Address");
        TextField port = new TextField();
        port.setPromptText("Port");

        grid.add(new Label("IP:"), 0, 0);
        grid.add(ip, 1, 0);
        grid.add(new Label("Port:"), 0, 1);
        grid.add(port, 1, 1);

        // set the grid a child of the dialog
        dialog.getDialogPane().setContent(grid);

        // Convert the result to a ip-port-pair when the connectButton IS CLICKED
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType)
                return new Pair<>(ip.getText(), port.getText());
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        if (!result.isPresent())
            return;

        pathCalculatorServerIP.set(result.get().getKey());
        pathCalculatorServerPORT.set(result.get().getValue());

        viewModel.calculatePath();
    }

    // opens an autopilot script from a file and copy its contents to the autoPilotScript text area

    public void onLoadScript(ActionEvent actionEvent)
    {
        // show dialog to open script file
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("Choose script");
        fileDialog.setInitialDirectory(new File("."));
        fileDialog.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FlightScript files (*.fs)", "*.fs"));

        File script = fileDialog.showOpenDialog(mapDisplayer.getScene().getWindow()); // 1 way to get primary window is through an item in that window...
        if (script == null)
            return; // do nothing if no file was chosen

        try
        {
            // copy script contents to the textArea of the script
            String[] scriptLines = Files.readAllLines(script.toPath()).toArray(new String[0]);
            this.autoPilotScriptTextArea.setText(String.join("\n", scriptLines));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        autoPilotRadioButton.setDisable(false);
    }

    // this event happens when the button is checked and is NOT already checked before!
    public void onAutoPilotRadio(ActionEvent actionEvent)
    {
        isAutoPilotMode.set(true);
        System.out.println("Auto pilot");
        viewModel.asyncRunAutoPilot(); // starts a thread that interprets the autopilot script
    }

    // this event happens when the button is checked and is NOT already checked before!
    public void onManualRadio(ActionEvent actionEvent)
    {
        isAutoPilotMode.set(false);
        System.out.println("Manual");
        viewModel.asyncJoystickPuller(); // starts a thread that updates the simulator about the joysticks current state
    }

    public void onTextChanged(KeyEvent keyEvent)
    {
        if (((TextArea) keyEvent.getSource()).getText().length() != 0)
            this.autoPilotRadioButton.setDisable(false);
        else
            this.autoPilotRadioButton.setDisable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) // the constructor happens once compared to initialize
    {
        mapDisplayer.isMousePressed.addListener((observable, isClickedOld, isClickedNew) -> {
            if (isClickedNew)
                if (pathCalculatorServerIP.get() != null && pathCalculatorServerIP.get().length() != 0 && pathCalculatorServerPORT.get() != null && pathCalculatorServerPORT.get().length() != 0)
                    viewModel.calculatePath();
        });
    }

    @Override
    public void update(Observable o, Object arg)
    {
    }
}
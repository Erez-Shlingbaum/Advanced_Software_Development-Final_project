package View;

import ViewModel.ViewModel;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MainWindowController implements Observer, Initializable
{
	@FXML
	RadioButton autoPilotRadioButton;
	BooleanProperty isAutoPilotMode; // if true the autopilot mode, else - manual mode

	/*TODO: change the example to the CSV file*/
	double[][] mapData = // TODO move this to map displayer file
			{
					{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
					{0, 0, 0, 1, 2, 4, 6, 8, 10, 12, 14, 12, 10, 8, 6, 4},
					{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 6, 2, 0, 0, 0, 0},
					{15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0},
					{12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 0, 0},
					{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5},
					{6, 8, 10, 12, 14, 12, 10, 8, 6, 4, 0, 0, 0, 1, 2, 4},
					{6, 7, 8, 9, 6, 2, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5},
					{9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 15, 14, 13, 12, 11, 10},
					{6, 5, 4, 3, 2, 1, 0, 0, 0, 0, 12, 11, 10, 9, 8, 7},
					{1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1, 0, 0, 0, 0, 0},
					{6, 6, 5, 5, 4, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0},
					{7, 6, 5, 4, 3, 2, 2, 1, 1, 1, 2, 3, 4, 5, 6, 7},
					{7, 6, 5, 4, 3, 2, 2, 1, 1, 1, 2, 3, 4, 5, 6, 7}
			};
	double max = findMax(); // TODO move this to map displayer file
	double min = findMin(); // TODO move this to map displayer file


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
	public IntegerProperty xStartIndex, yStartIndex; // indexes in MATRIX
	public IntegerProperty xEndIndex, yEndIndex;     // indexes in MATRIX

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
		xStartIndex = new SimpleIntegerProperty();
		yStartIndex = new SimpleIntegerProperty();
		xEndIndex = new SimpleIntegerProperty();
		yEndIndex = new SimpleIntegerProperty();
	}

	//initials min
	double findMax() // TODO move this to map displayer file
	{
		double tMax = mapData[0][0];
		for (int i = 0; i < this.mapData.length; i++)
		{
			for (int j = 0; j < this.mapData[i].length; j++)
			{
				if (tMax < this.mapData[i][j])
					tMax = this.mapData[i][j];
			}
		}
		return tMax;
	}

	//initials max
	double findMin() // TODO move this to map displayer file
	{
		double tMin = mapData[0][0];
		for (int i = 0; i < this.mapData.length; i++)
		{
			for (int j = 0; j < this.mapData[i].length; j++)
			{
				if (this.mapData[i][j] < tMin)
					tMin = this.mapData[i][j];
			}
		}
		return tMin;
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
		//mapDisplayer.mapData.bind(viewModel.heightsInMetersMatrix);            //  uncomment this to get map data from csv file

		// path solving
		viewModel.pathCalculatorServerIP.bind(pathCalculatorServerIP);
		viewModel.pathCalculatorServerPORT.bind(pathCalculatorServerPORT);

		viewModel.xStartIndex.bind(xStartIndex);
		viewModel.yStartIndex.bind(yStartIndex);
		viewModel.xEndIndex.bind(xEndIndex);
		viewModel.yEndIndex.bind(yEndIndex);

		// map displayer
		//TODO: change to PlaneDisplayer
		mapDisplayer.pathToEndCoordinate.bind(viewModel.pathToEndCoordinate); // getting solution from ptm1 server

		// joystick
		viewModel.isAutoPilotMode.bind(isAutoPilotMode);
		viewModel.xAxisJoystick.bind(joystickController.xAxisJoystick);
		viewModel.yAxisJoystick.bind(joystickController.yAxisJoystick);
		viewModel.rudderJoystick.bind(joystickController.downSlider.valueProperty());
		viewModel.throttleJoystick.bind(joystickController.leftSlider.valueProperty());
	}

	public void onConnectToSimulator(ActionEvent actionEvent)
	{
		//TODO
		// show popup dialog and get (ip,port) from that dialog
		// send viewModel a command to connect to client
	}

	public void onOpenData(ActionEvent actionEvent)
	{
		//TODO
		// show dialog and get csv file path
		// ask viewModel openCsvFile
		// use mapDisplayer to display the data
		// open a Thread tha 4 times per second will get from the viewmodel the current plane coordinates and update the mapDisplayer.planeLocation

		// show dialog to open script file
		FileChooser fileDialog = new FileChooser();
		fileDialog.setTitle("Open a csv file");
		fileDialog.setInitialDirectory(new File("."));
		fileDialog.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));

		File script = fileDialog.showOpenDialog(mapDisplayer.getScene().getWindow()); // 1 way to get primary window is through an item in that window...
		if (script == null)
			return; // do nothing if no file was chosen

		csvFilePath.set(script.getPath());
		viewModel.openCsvFile();

		//mapDisplayer.redraw(); TODO for map displayer to draw csv
	}

	public void onCalculatePath(ActionEvent actionEvent)
	{
		//TODO
		// show popup dialog and get (ip,port) of the shortestPathServer we did in PTM1
		// ask viewmodel to connect to shortestPathServer with the mapDisplayer.matrix
		// update mapDisplayer with the new path and target location
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
		try
		{
			viewModel.asyncRunAutoPilot(); // starts a thread that interprets the autopilot script
		} catch (Exception e)
		{
			new Alert(Alert.AlertType.ERROR, "script error:\n" + e.getLocalizedMessage()).show();    // TODO delete this because it does not work
		}
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
	public void initialize(URL location, ResourceBundle resources)/*the controller happens once compared to initialize*/
	{
		mapDisplayer.setMapData(mapData, max, min);
	}

	@Override
	public void update(Observable o, Object arg)
	{

	}
}

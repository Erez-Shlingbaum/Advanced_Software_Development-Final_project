package ViewModel;

import Model.IModel;
import javafx.beans.property.*;

import java.util.Observable;
import java.util.Observer;


// TODO idea - hold ThreadPool/ThreadManger in this class and make every call to the model Asynchronous

public class ViewModel extends Observable implements Observer
{
	private IModel interpreterModel;

	public BooleanProperty isAutoPilotMode;

	//javaFX properties
	public StringProperty scriptToInterpret;
	public StringProperty commandName, commandArguments;

	// csv file
	public StringProperty csvFilePath;
	public DoubleProperty xCoordinateLongitude;
	public DoubleProperty yCoordinateLatitude;
	public DoubleProperty cellSizeInDegrees;
	public ObjectProperty<double[][]> heightsInMetersMatrix; // matrix of heights read from thte csv file

	// information about the plane (for map displayer
	public DoubleProperty currentPlaneLongitudeX;
	public DoubleProperty currentPlaneLatitudeY;

	// properties for path searching
	public StringProperty pathCalculatorServerIP;
	public StringProperty pathCalculatorServerPORT;

	public IntegerProperty xStartIndex, yStartIndex; // indexes in MATRIX
	public IntegerProperty xEndIndex, yEndIndex;     // indexes in MATRIX
	public StringProperty pathToEndCoordinate;         // for example: "Right,Up,Right,Left,Down" ...

	// connection to simulator
	public StringProperty simulatorIP;
	public StringProperty simulatorPort;

	// properties for joystick
	public DoubleProperty xAxisJoystick;
	public DoubleProperty yAxisJoystick;
	public DoubleProperty rudderJoystick;
	public DoubleProperty throttleJoystick;

	// var to hold getVars from script
	public DoubleProperty varRetrieved;


	public ViewModel(IModel model)
	{
		this.interpreterModel = model;
		//initialize properties
		scriptToInterpret = new SimpleStringProperty();
		commandName = new SimpleStringProperty();
		commandArguments = new SimpleStringProperty();

		// csv
		csvFilePath = new SimpleStringProperty();
		xCoordinateLongitude = new SimpleDoubleProperty();
		yCoordinateLatitude = new SimpleDoubleProperty();
		cellSizeInDegrees = new SimpleDoubleProperty();
		heightsInMetersMatrix = new SimpleObjectProperty<>();

		// for info about the plane
		currentPlaneLongitudeX = new SimpleDoubleProperty();
		currentPlaneLatitudeY = new SimpleDoubleProperty();

		//path searching
		pathCalculatorServerIP = new SimpleStringProperty();
		pathCalculatorServerPORT = new SimpleStringProperty();
		xStartIndex = new SimpleIntegerProperty();
		yStartIndex = new SimpleIntegerProperty();
		xEndIndex = new SimpleIntegerProperty();
		yEndIndex = new SimpleIntegerProperty();

		pathToEndCoordinate = new SimpleStringProperty();    // will hold solution

		// connection to simulator
		simulatorIP = new SimpleStringProperty();
		simulatorPort = new SimpleStringProperty();
		// joystick
		isAutoPilotMode = new SimpleBooleanProperty();
		xAxisJoystick = new SimpleDoubleProperty();
		yAxisJoystick = new SimpleDoubleProperty();
		rudderJoystick = new SimpleDoubleProperty();
		throttleJoystick = new SimpleDoubleProperty();

		// utility
		varRetrieved = new SimpleDoubleProperty();
	}

	// allows interpreting a script from the view
	public void interpretScript()
	{
		// getting the script and splitting it into lines
		interpreterModel.interpretScript(scriptToInterpret.get().split("\\n"));
	}

	// allows executing a command in the model with parameters
	public void executeCommand()
	{
		interpreterModel.executeCommand(commandName.get(), commandArguments.get().split(" ")); // assume arguments are sent with space between them as 1 string
	}

	// calculate shortest path for the plane
	public void calculatePath()
	{
		// send for calculation the problem solver server and the problem to solve
		interpreterModel.calculatePath
				(
						pathCalculatorServerIP.get(),
						pathCalculatorServerPORT.get(),
						heightsInMetersMatrix.get(),
						new int[]{xStartIndex.get(), yStartIndex.get()},
						new int[]{xEndIndex.get(), yEndIndex.get()}
				);
	}

	public void asyncMapPlanePositionUpdater()
	{
		new Thread(() -> {
			interpreterModel.retrieveVariableInScript("longitude");
			currentPlaneLongitudeX.set(interpreterModel.getVarRetrivedFromScript());

			interpreterModel.retrieveVariableInScript("latitude");
			currentPlaneLatitudeY.set(interpreterModel.getVarRetrivedFromScript());

			try
			{
				Thread.sleep(250); // 4 times per second
			} catch (InterruptedException e) {e.printStackTrace();}
		}).start();
	}

	// starts a thread that updates the simulator about the joysticks current state
	public void asyncJoystickPuller()
	{
		new Thread(() -> {
			while (!isAutoPilotMode.get())
			{
				if (interpreterModel.isConnectedToSimulator())
					interpreterModel.sendJoystickState(xAxisJoystick.get(), yAxisJoystick.get(), rudderJoystick.get(), throttleJoystick.get());
				try {Thread.sleep(250); } catch (InterruptedException e) {e.printStackTrace(); }
			}
		}).start();
	}

	public void asyncRunAutoPilot()
	{
		// run script in a thread, but to have the abbility to stop the execution, we have another thread to check if we need to stop
		Thread scriptExecution = new Thread(() -> this.interpretScript());
		scriptExecution.start();

		// when no longer autopilot, stop the executing thread
		// in addition, for aesthetic reasons, update the joystick with the current values from the simulator
		new Thread(() -> {
			while (isAutoPilotMode.get())
				try
				{
					/*
					// update joystick		(will only work if biDirectional bind exist)
					interpreterModel.retrieveVariableInScript("rudder");
					rudderJoystick.set(varRetrieved.get());

					interpreterModel.retrieveVariableInScript("throttle");
					throttleJoystick.set(varRetrieved.get());

					interpreterModel.retrieveVariableInScript("aileron");	// TODO fix this. joystick wont update position because we need to call a redraw function (this i s complicated)
					xAxisJoystick.set(varRetrieved.get());

					interpreterModel.retrieveVariableInScript("elevator");  // TODO fix this. joystick wont update position because we need to call a redraw function (this i s complicated)
					yAxisJoystick.set(varRetrieved.get()); */
					// sleep so this thread will run 4 times per second
					Thread.sleep(250);
				} catch (InterruptedException e) {e.printStackTrace(); }
			// no longer auto pilot mode.
			scriptExecution.interrupt();
		}).start();
	}

	public void openCsvFile()
	{
		interpreterModel.openCsvFile(csvFilePath.get());
	}

	@Override
	public void update(Observable o, Object arg)
	{
		if (o == interpreterModel)
		{
			String message = (String) arg; // It is better to use enum classes instead of strings - because it will be easier to refactor and change names

			// when model has finished doing something, we are notified and can choose to do do something about it (like updating a property in the view)
			switch (message)
			{
				case "calculatedPath":
					this.pathToEndCoordinate.set(interpreterModel.getSolutionForPathProblem());
					break;
				case "scriptInterpreted":
					// do something
					break;
				case "commandExecuted":
					// do something
					break;
				case "csvScanned":
					this.xCoordinateLongitude.set(interpreterModel.getxCoordinateLongitude());
					this.yCoordinateLatitude.set(interpreterModel.getyCoordinateLatitude());
					this.cellSizeInDegrees.set(interpreterModel.getCellSizeInDegrees());
					this.heightsInMetersMatrix.set(interpreterModel.getCsvValues());
					break;
				case "varRetrieved":
					this.varRetrieved.set(interpreterModel.getVarRetrivedFromScript());
					break;
			}
		}
	}

	public void connectToSimulator()
	{
		interpreterModel.executeCommand("connect", simulatorIP.get(), simulatorPort.get());
	}
}

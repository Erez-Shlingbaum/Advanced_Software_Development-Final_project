package ViewModel;

import Model.IModel;
import javafx.beans.property.*;

import java.util.Observable;
import java.util.Observer;


// TODO idea - hold ThreadPool/ThreadManger in this class and make every call to the model Asynchronous

public class ViewModel extends Observable implements Observer
{
	private IModel interpreterModel;

	//javaFX properties
	public StringProperty scriptToInterpret;
	public StringProperty commandName, commandArguments;

	// properties for path searching
	public ObjectProperty<double[][]> heightsInMetersMatrix; // this will hold the problem state as a matrix of heights
	public ObjectProperty<int[]> startCoordinate, endCoordinate;
	public StringProperty pathCalculatorServerIP;
	public IntegerProperty pathCalculatorServerPORT;
	public StringProperty pathToEndCoordinate; // for example: "Right,Up,Right,Left,Down" ...

	public ViewModel(IModel model)
	{
		this.interpreterModel = model;
		//initialize properties
		scriptToInterpret = new SimpleStringProperty();
		commandName = new SimpleStringProperty();
		commandArguments = new SimpleStringProperty();

		//path searching
		heightsInMetersMatrix = new SimpleObjectProperty<>();
		startCoordinate = new SimpleObjectProperty<>();
		endCoordinate = new SimpleObjectProperty<>();

		pathCalculatorServerIP = new SimpleStringProperty();
		pathCalculatorServerPORT = new SimpleIntegerProperty();

		pathToEndCoordinate = new SimpleStringProperty();
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
		interpreterModel.calculatePath(pathCalculatorServerIP.get(),
				pathCalculatorServerPORT.get(),
				heightsInMetersMatrix.get(),
				startCoordinate.get(),
				endCoordinate.get());
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
					// do something
					break;
			}
		}
	}
}

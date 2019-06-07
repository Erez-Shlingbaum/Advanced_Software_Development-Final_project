package ViewModel;

import Model.IModel;
import javafx.beans.property.*;

import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer
{
	private IModel interpreterModel;

	//javaFX preoperties
	public StringProperty scriptToInterpret;
	public StringProperty commandName, commandArguments;

	// for path searching
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

	// interpreting a script from the view
	public void interpretScript()
	{
		interpreterModel.interpretScript(scriptToInterpret.get().split("\\n"));
	}

	// for reuse of code
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
		if(o == interpreterModel)
		{
			String message = (String)arg;
			switch (message)
			{
				case "calculatedPath":
					this.pathToEndCoordinate.set(interpreterModel.getCalculatedPath());
					break;
			}
		}
	}
}

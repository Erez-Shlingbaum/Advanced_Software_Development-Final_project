package ViewModel;

import Model.IModel;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer
{
    // Property to differintiate bewtween modes in the GUI
    public final BooleanProperty isAutoPilotMode = new SimpleBooleanProperty();
    // General properties
    public final StringProperty scriptToInterpret = new SimpleStringProperty();
    public final StringProperty commandName = new SimpleStringProperty();
    public final StringProperty commandArguments = new SimpleStringProperty();
    // Properties who will be initialized from csv file
    public final StringProperty csvFilePath = new SimpleStringProperty();
    public final DoubleProperty xCoordinateLongitude = new SimpleDoubleProperty();
    public final DoubleProperty yCoordinateLatitude = new SimpleDoubleProperty();
    public final DoubleProperty cellSizeInDegrees = new SimpleDoubleProperty();
    public final ObjectProperty<double[][]> heightsInMetersMatrix = new SimpleObjectProperty<>(); // matrix of heights read from the csv file
    // Real-time information about the plane for map displayer
    public final DoubleProperty currentPlaneLongitudeX = new SimpleDoubleProperty();
    public final DoubleProperty currentPlaneLatitudeY = new SimpleDoubleProperty();
    // Path searching properties for calulation of shortest path
    public final StringProperty pathCalculatorServerIP = new SimpleStringProperty();
    public final StringProperty pathCalculatorServerPORT = new SimpleStringProperty();
    // Indeces in height matrix
    public final IntegerProperty xStartIndex = new SimpleIntegerProperty();
    public final IntegerProperty yStartIndex = new SimpleIntegerProperty();
    public final IntegerProperty xEndIndex = new SimpleIntegerProperty();
    public final IntegerProperty yEndIndex = new SimpleIntegerProperty();
    // Property to hold solution retrieved from search problem server
    public final StringProperty pathToEndCoordinate = new SimpleStringProperty();         // for example: "Right,Up,Right,Left,Down" ...
    // Connection to simulator properties
    public final StringProperty simulatorIP = new SimpleStringProperty();
    public final StringProperty simulatorPort = new SimpleStringProperty();
    // Joystick properties
    public final DoubleProperty xAxisJoystick = new SimpleDoubleProperty();
    public final DoubleProperty yAxisJoystick = new SimpleDoubleProperty();
    public final DoubleProperty rudderJoystick = new SimpleDoubleProperty();
    public final DoubleProperty throttleJoystick = new SimpleDoubleProperty();
    // Utility var to hold model.getVar("name") from script
    public final DoubleProperty varRetrieved = new SimpleDoubleProperty();
    private final IModel interpreterModel;


    public ViewModel(IModel model)
    {
        this.interpreterModel = model;
    }

    public void interpretScript()
    {
        interpreterModel.interpretScript(scriptToInterpret.get().split("\\n"));
    }

    // Allows executing a single command in the interpreter
    public void executeCommand()
    {
        interpreterModel.executeCommand(commandName.get(), commandArguments.get().split(" "));
    }

    // Calculate shortest path for the plane
    public void calculatePath()
    {
        new Thread(() -> {
            interpreterModel.calculatePath
                    (
                            pathCalculatorServerIP.get(),
                            pathCalculatorServerPORT.get(),
                            heightsInMetersMatrix.get(),
                            new int[]{xStartIndex.get(), yStartIndex.get()},
                            new int[]{xEndIndex.get(), yEndIndex.get()}
                    );
        }).start();
    }

    // Starts thread that updates the map about the simulator current state
    public void asyncMapPlanePositionUpdater()
    {
        new Thread(() -> {
            while (true)
            {
                interpreterModel.retrieveVariableInScript("longitude");
                currentPlaneLongitudeX.set(interpreterModel.getVarRetrivedFromScript());

                interpreterModel.retrieveVariableInScript("latitude");
                currentPlaneLatitudeY.set(interpreterModel.getVarRetrivedFromScript());

                try
                {
                    Thread.sleep(250); // 4 times per second
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Starts a thread that updates the simulator about the joysticks current state
    public void asyncJoystickPuller()
    {
        new Thread(() -> {
            while (!isAutoPilotMode.get())
            {
                if (interpreterModel.isConnectedToSimulator())
                    interpreterModel.sendJoystickState(xAxisJoystick.get(), yAxisJoystick.get(), rudderJoystick.get(), throttleJoystick.get());
                try
                {
                    Thread.sleep(150);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void asyncRunAutoPilot()
    {
        // Run script in a thread
        Thread scriptExecution = new Thread(this::interpretScript);
        scriptExecution.start();

        // thread to check if GUI is no longer autopilot, if yes the stop the executing thread and die
        new Thread(() -> {
            while (isAutoPilotMode.get())
                try
                {
                    Thread.sleep(250);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            scriptExecution.interrupt(); // This exception is catched in the interpreter
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

            // When model has finished doing something, we are notified and can choose to do do something about it (like updating a property in the view)
            switch (message)
            {
                case "calculatedPath":
                    Platform.runLater(() -> this.pathToEndCoordinate.set(interpreterModel.getSolutionForPathProblem())); // This is done to fix a bug when a thread whose not the javafx thread try to change UI elements (later in map drawer...) and because of that, an exception is thrown
                    break;
                case "scriptInterpreted":
                    // Do something
                    break;
                case "commandExecuted":
                    // Do something
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

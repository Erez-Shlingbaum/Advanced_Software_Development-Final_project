package Model;

import Model.Interpreter.Client_Side.ConnectClient;
import Model.Interpreter.Commands.*;
import Model.Interpreter.Interpeter.Interpreter;
import Model.Interpreter.Interpeter.InterpreterContext;
import Model.Interpreter.Interpeter.Variable;
import Model.Interpreter.Server_Side.DataServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class InterpreterModel extends Observable implements IModel
{
    // interpreter instance
    private final Interpreter interpreter;

    // return values
    private String solutionForPathProblem;
    private int returnValue; // this is set to 0 if a script/command did not return something

    // csv parameters
    private double xCoordinateLongitude;
    private double yCoordinateLatitude; // longitude, latitude of (0,0) index in the csv table
    private double cellSizeInDegrees;  // size of each cell in the csv table in degrees (this is related to longitude and latitude)
    private double[][] csvValues = null;
    private double varRetrivedFromScript;

    public InterpreterModel()
    {
        HashMap<String, Command> keywords = new HashMap<>();

        keywords.put("openDataServer", new OpenServerCommand());
        keywords.put("connect", new ConnectCommand());
        keywords.put("var", new DefineVarCommand());
        keywords.put("print", new PrintCommand());
        keywords.put("sleep", new SleepCommand());
        keywords.put("while", new WhileCommand());
        keywords.put("return", new ReturnCommand());
        keywords.put("disconnect", new DisconnectCommand());
        keywords.put("pause", new PauseCommand());

        InterpreterContext context = new InterpreterContext(keywords, new ConnectClient(), new DataServer());

        // adding 2 variables for the sake of getting them from the simulator
        Variable longitude = new Variable(context);
        Variable latitude = new Variable(context);
        longitude.setPath("/position/longitude-deg");
        latitude.setPath("/position/latitude-deg");

        context.symbolTable.put("longitude", longitude);
        context.symbolTable.put("latitude", latitude);

        this.interpreter = new Interpreter(context);
    }

    // Read lines from file and send them to interpretScript(String[])
    @Override
    public void interpretScript(String filePath)
    {
        List<String> lines = new LinkedList<>();
        try
        {
            // Create a list of simulator bind paths from file (the order of the paths is important)
            Scanner linesScanner = new Scanner(new File(filePath));
            while (linesScanner.hasNextLine())
                lines.add(linesScanner.nextLine());
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        this.interpretScript(lines.toArray(new String[0]));
    }

    @Override
    public void interpretScript(String... scriptLines)
    {
        returnValue = this.interpreter.interpret(scriptLines); // Bonus: each line interpreted is highlighted in view	// IDEA - use help function interpretLine and after each line, notify GUI about line read.
        super.setChanged();
        super.notifyObservers("scriptInterpreted");
    }

    @Override
    public void executeCommand(String cmdName, String... args)
    {
        returnValue = this.interpreter.interpret(new String[]{cmdName + " " + String.join(" ", args)}); // create one liner script to interpret
        super.setChanged();
        super.notifyObservers("commandExecuted");
    }

    @Override
    public void retrieveVariableInScript(String variableName)
    {
        varRetrivedFromScript = interpreter.getVariableFromScript(variableName);
        super.setChanged();
        super.notifyObservers("varRetrieved");
    }

    @Override
    public double getVarRetrivedFromScript()
    {
        return varRetrivedFromScript;
    }

    @Override
    public int getReturnValue()
    {
        return this.returnValue;
    }

    @Override
    public void calculatePath(String ip, String port, double[][] heightsInMeters, int[] startCoordinateIndex, int[] endCoordinateIndex)
    {
        try
        {
            System.out.println("col plane: " + startCoordinateIndex[0]);
            System.out.println("row plane: " + startCoordinateIndex[1]);

            System.out.println("col target: " + endCoordinateIndex[0]);
            System.out.println("row target: " + endCoordinateIndex[1]);

            Socket server = new Socket(ip, Integer.parseInt(port));
            PrintWriter writer = new PrintWriter(server.getOutputStream()); //remember to flush output!
            Scanner inputFromServer = new Scanner(server.getInputStream());

            String problem = convertMatrixToString(heightsInMeters);

            // send problem to server

            writer.print(problem); // problem string already includes necessary \n
            writer.println("end");
            writer.println(startCoordinateIndex[1] + "," + startCoordinateIndex[0]);
            writer.println(endCoordinateIndex[1] + "," + endCoordinateIndex[0]);
            writer.flush();
            // receive solution as a string "Right,Left,......"

            this.solutionForPathProblem = inputFromServer.nextLine();
            System.out.println(this.solutionForPathProblem);

            // notify viewModel that we finished computing path
            super.setChanged();
            super.notifyObservers("calculatedPath");

            writer.close();
            inputFromServer.close();
            server.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String convertMatrixToString(double[][] heightsInMeters)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (double[] heightsInMeter : heightsInMeters)
        {
            // Loop through all elements of current row
            for (double v : heightsInMeter) stringBuilder.append(v).append(',');
            stringBuilder.setLength(stringBuilder.length() - 1); // "remove" last comma
            stringBuilder.append('\n');
        }

        // replacing 0's so that the BFS algorithm wont choose greedily incredibly long paths. because of height 0
        return stringBuilder.toString().replaceAll("0", "20");
    }

    @Override
    public void openCsvFile(String filePath)
    {
        int sizeRows = 0, sizeColumns = 0;

        // initialize parameters
        try
        {
            sizeRows = (int) Files.lines(Paths.get(filePath), Charset.defaultCharset()).count();
            Scanner counterColumns = new Scanner(new File(filePath));
            // go past first 2 values
            counterColumns.nextLine();
            counterColumns.nextLine();
            sizeColumns = counterColumns.nextLine().split(",").length;

            csvValues = new double[sizeRows][sizeColumns];

            counterColumns.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        // read csv file fully
        try
        {
            Scanner csvScanner = new Scanner(new File(filePath));
            csvScanner.useDelimiter(",");

            xCoordinateLongitude = csvScanner.nextDouble();
            yCoordinateLatitude = csvScanner.nextDouble();
            csvScanner.nextLine(); // go past empty ",,,,,,"

            cellSizeInDegrees = csvScanner.nextDouble();
            csvScanner.nextLine(); // go past empty ",,,,,,"

            for (int rowIndex = 0; rowIndex < sizeRows; rowIndex++)
                for (int columnIndex = 0; columnIndex < sizeColumns; columnIndex++)
                    if (csvScanner.hasNextDouble())
                        csvValues[rowIndex][columnIndex] = csvScanner.nextDouble();
                    else if (csvScanner.hasNextLine())
                        csvScanner.nextLine();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        super.setChanged();
        super.notifyObservers("csvScanned");
    }

    @Override
    public void sendJoystickState(double aileron, double elevator, double rudder, double throttle)
    {
        //this.executeCommand("connect", "127.0.0.1", "5402");
        System.out.println("DEBUG: " + aileron + "," + elevator + "," + rudder + "," + throttle);
        interpreter.setVariableInSimulator("/controls/flight/aileron", aileron);
        interpreter.setVariableInSimulator("/controls/flight/elevator", elevator);
        interpreter.setVariableInSimulator("/controls/flight/rudder", rudder);
        interpreter.setVariableInSimulator("/controls/engines/current-engine/throttle", throttle);

    }

    @Override
    public Boolean isConnectedToSimulator()
    {
        return interpreter.isConnectedToSimulator();
    }

    @Override
    public String getSolutionForPathProblem()
    {
        return this.solutionForPathProblem;
    }

    @Override
    public double getxCoordinateLongitude()
    {
        return xCoordinateLongitude;
    }

    @Override
    public double getyCoordinateLatitude()
    {
        return yCoordinateLatitude;
    }

    @Override
    public double getCellSizeInDegrees()
    {
        return cellSizeInDegrees;
    }

    @Override
    public double[][] getCsvValues()
    {
        return csvValues;
    }
}
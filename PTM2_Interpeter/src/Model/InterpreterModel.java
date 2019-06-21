package Model;

import Model.Interpreter.test.MyInterpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Scanner;

public class InterpreterModel extends Observable implements IModel
{
	// interpreter instance
	private MyInterpreter interpreter;

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
		this.interpreter = new MyInterpreter();
	}

	// read lines from file and send them to interpretScript(String[])
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
		} catch (FileNotFoundException e) {e.printStackTrace();}
		this.interpretScript(lines.toArray(new String[0]));
	}

	@Override
	public void interpretScript(String... scriptLines)
	{
		// in a different thread..
		returnValue = this.interpreter.interpret(scriptLines); // Bonus: each line interpreted is highlighted in view
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
	public int getReturnValue() { return this.returnValue; }

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
		} catch (IOException e) { e.printStackTrace(); }
	}

	private String convertMatrixToString(double[][] heightsInMeters)
	{
		StringBuilder stringBuilder = new StringBuilder();

		for (double[] heightsInMeter : heightsInMeters) {
			for (int j = 0; j < heightsInMeter.length; j++) // Loop through all elements of current row
				stringBuilder.append(heightsInMeter[j]).append(',');
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
		} catch (IOException e) { e.printStackTrace(); }

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
		} catch (FileNotFoundException e) {e.printStackTrace();}

		super.setChanged();
		super.notifyObservers("csvScanned");
	}

	@Override
	public void sendJoystickState(double aileron, double elevator, double rudder, double throttle)
	{
		//this.executeCommand("connect", "127.0.0.1", "5402");
		System.out.println("DEBUG: " + aileron + "," + elevator + "," + rudder+ "," + throttle);
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
	public String getSolutionForPathProblem() { return this.solutionForPathProblem; }

	@Override
	public double getxCoordinateLongitude() { return xCoordinateLongitude; }

	@Override
	public double getyCoordinateLatitude() { return yCoordinateLatitude; }

	@Override
	public double getCellSizeInDegrees() { return cellSizeInDegrees; }

	@Override
	public double[][] getCsvValues() { return csvValues; }

	public static void main(String[] args)
	{
		// Test functionality of code above me
		InterpreterModel interpreterModel = new InterpreterModel();

        //System.out.println(interpreterModel.convertMatrixToString(new double[][] { {4} , {9,8,7,6}}));

		// test calculator with real numbers
		/*
		interpreterModel.interpretScript(
				"var x = 6.0",
				"var y = 10.5",
				"return y*x");
		System.out.println(interpreterModel.returnValue); // expecting '20'
		*/
        /*

        // Testing "executeCommand"
        interpreterModel.executeCommand("return", "1+", "2+", "3");
        System.out.println(interpreterModel.returnValue); // expecting '6'

        //// Testing "InterpretScript"  2 ways
        // first way
        String[] test5 = {
                "var x = 0",
                "var y = " + 10,
                "while x < 5 {",
                "	y = y + 2",
                "	x = x + 1",
                "}",
                "return y"
        };
        interpreterModel.interpretScript(test5);
        System.out.println(interpreterModel.returnValue); // expecting '20'

        // second way
        interpreterModel.interpretScript(
                "var x = 0",
                "var y = 10",
                "while x < 5 {",
                "	y = y + 2",
                "	x = x + 1",
                "}",
                "return y");
        System.out.println(interpreterModel.returnValue); // expecting '20'

       */


		// Testing "calculatePath" on our server(PTM1) on port 5555
		// before testing this, run runServer.bat!
		interpreterModel.openCsvFile("./PTM2_Interpeter/Resources/map-Honolulu.csv");
		//System.out.println("col " + interpreterModel.csvValues.length + " row " + interpreterModel.csvValues[0].length);
		interpreterModel.calculatePath(
				"127.0.0.1",
				"5555",
				interpreterModel.getCsvValues(),
				new int[]{17, 80},     // start point
				new int[]{153, 246});   // end point

		System.out.println(interpreterModel.solutionForPathProblem);


        /*
		// test calculator with negative numbers
		// test 1
		interpreterModel.interpretScript(
				"var x = -5",
				"while x < 5 {",
				"	print x",
				"	x = x + 1",
				"}",
				"return -x");
		System.out.println(interpreterModel.returnValue); // expecting "-5 , ..... , 4" , retValue = '-5'
		*/
		// test 2

		/*
		interpreterModel.interpretScript(
				"var roll = -6",
				"var aileron = - roll / 70",
				"print aileron",
				"return aileron");
		System.out.println(interpreterModel.returnValue); // expecting '0.085'
		*/
		// test openDataServer with flight gear (please open flight gear for this test!)
		//interpreterModel.interpretScript("./PTM2_Interpeter/Resources/autopilot_script.fs");
	}
}
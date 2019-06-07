package Model;

import Model.test.MyInterpreter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;

public class interpreterModel extends Observable implements  IModel
{
	String solutionForPathProblem;

	// TODO: add optimization in interpreter -> don't create a new keyWords map each call to interpret...!! (make it static)

	@Override
	public void interpretScript(String[] lines)
	{
		// in a different thread..
		MyInterpreter.interpret(lines); // Bonus: each line interpreted is highlighted in view
	}

	@Override
	public void executeCommand(String cmdName, String[] args)
	{
		MyInterpreter.interpret(new String[]{cmdName + String.join(" ", args)}); // create one liner script to interpret
	}

	@Override
	public void calculatePath(String ip, int port, double[][] heightsInMeters, int[] startCoordinate, int[] endCoordinate)
	{
		try
		{
			Socket server = new Socket(ip,port);
			PrintWriter writer = new PrintWriter(server.getOutputStream()); //remember to flush output!

			String problem = convertMatrixToString(heightsInMeters);

			// send problem to serve
			writer.print(problem); // problem string already includes necessary \n
			writer.println(startCoordinate[0] + "," + startCoordinate[1]);
			writer.println(endCoordinate[0] + "," + endCoordinate[1]);

			// TODO: receive (preferably in a different thread...) solution in form of {left,right.....}
			//this.solutionForPathProblem = ;// TODO: put solution in variable to hold it, so getCalcPath can return it

			// notify viewModel that we finished computing path
			super.setChanged();
			super.notifyObservers();
		} catch (IOException e) { e.printStackTrace(); }
	}
	private String convertMatrixToString(double[][] heightsInMeters) // TODO: test this method
	{
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < heightsInMeters.length; i++) // Loop through all rows
		{
			for (int j = 0; j < heightsInMeters[i].length; j++) // Loop through all elements of current row
				stringBuilder.append(heightsInMeters[i][j]).append(',');
			stringBuilder.setLength(stringBuilder.length() - 1); // "remove" last comma
			stringBuilder.append('\n');
		}
		return stringBuilder.toString();
	}

	@Override
	public String getCalculatedPath()
	{
		return this.solutionForPathProblem;
	}
}

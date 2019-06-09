package Model;

import Model.Expressions.Calculator;
import Model.Expressions.PreCalculator;
import Model.test.MyInterpreter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.Scanner;

public class InterpreterModel extends Observable implements IModel
{
    // interpreter instance
    MyInterpreter interpreter;

    // return values
    String solutionForPathProblem;
    int returnValue; // this is set to 0 if a script/command did not return something

    public InterpreterModel()
    {
        this.interpreter = new MyInterpreter();
    }

    @Override
    public void interpretScript(String... scriptLines)
    {
        // in a different thread..
        returnValue = this.interpreter.interpret(scriptLines); // Bonus: each line interpreted is highlighted in view
        super.setChanged();
        super.notifyObservers();
    }

    @Override
    public void executeCommand(String cmdName, String... args)
    {
        returnValue = this.interpreter.interpret(new String[]{cmdName + " " + String.join(" ", args)}); // create one liner script to interpret
        super.setChanged();
        super.notifyObservers();
    }

    public int getReturnValue()
    {
        return this.returnValue;
    }

    @Override
    public void calculatePath(String ip, int port, double[][] heightsInMeters, int[] startCoordinate, int[] endCoordinate)
    {
        try
        {
            Socket server = new Socket(ip, port);
            PrintWriter writer = new PrintWriter(server.getOutputStream()); //remember to flush output!
            Scanner inputFromServer = new Scanner(server.getInputStream());

            String problem = convertMatrixToString(heightsInMeters);

            // send problem to server

            writer.print(problem); // problem string already includes necessary \n
            writer.println("end");
            writer.println(startCoordinate[0] + "," + startCoordinate[1]);
            writer.println(endCoordinate[0] + "," + endCoordinate[1]);
            writer.flush();
            // recive solution as a string "Right,Left,......"

            this.solutionForPathProblem = inputFromServer.nextLine();

            // notify viewModel that we finished computing path
            super.setChanged();
            super.notifyObservers();


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


    public static void main(String[] args)
    {
        // Test functionality of code above me
        InterpreterModel interpreterModel = new InterpreterModel();

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
        /*
        interpreterModel.calculatePath(
                "127.0.0.1",
                5555,
                new double[][]
                        {
                        {0,1,2,3},
                        {1,2,3,4},
                        {2,3,4,5},
                        {66,5,4,3}
                        },
                new int[]{0,0},     // start point
                new int[] {3,3});   // end point

        System.out.println(interpreterModel.solutionForPathProblem);
        */

        // test calculator with negative numbers
        interpreterModel.interpretScript(
                "var x = -5",
                "while x < 5 {",
                "	print x",
                "	x = x + 1",
                "}",
                "return -x");
        System.out.println(interpreterModel.returnValue); // expecting "-5 , ..... , 4" , retValue = '-5'
    }
}
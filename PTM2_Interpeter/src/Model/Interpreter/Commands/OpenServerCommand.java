package Model.Interpreter.Commands;

import Model.Interpreter.Expressions.Calculator;
import Model.Interpreter.Server_Side.DataClientHandler;
import Model.Interpreter.Server_Side.DataServer;

public class OpenServerCommand implements Command
{

    @Override
    public void execute(String[] args) throws Exception
    {
        //check exceptions
        if (args.length != 2)
            throw new Exception("Syntax error: openDataServer expects two arguments");
        if (DataServer.isReferenceExists())
            throw new Exception("Runtime error: DataServer already running");

        //calculate complex expressions
        int port = (int) Calculator.calculate(args[0]);            //IDEA: port must to be an integer, check if the answer incorrect
        int linesPerSecond = (int) Calculator.calculate(args[1]);//IDEA: linesPerSecond must to be an integer, check if the answer incorrect

        //System.out.println("port = "+ port);	//TODO: use this if needed
        //System.out.println("linerPer = "+ linesPerSecond);

        //creating the 'data server' and running it in a different thread
        DataServer dataServer = DataServer.getReference();
        dataServer.open(port, new DataClientHandler(linesPerSecond));

        Thread.sleep(2000); // for synchronization with Model.Interpreter.test system
    }
}

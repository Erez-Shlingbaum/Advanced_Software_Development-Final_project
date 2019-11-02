package Model.Interpreter.Commands;

import Model.Interpreter.Expressions.Calculator;
import Model.Interpreter.Interpeter.InterpreterContext;
import Model.Interpreter.Server_Side.DataClientHandler;
import Model.Interpreter.Server_Side.Server;

public class OpenServerCommand implements Command
{

    @Override
    public void execute(String[] args, InterpreterContext context) throws Exception
    {
        // Check exceptions
        if (args.length != 2)
            throw new Exception("Syntax error: openDataServer expects two arguments");
        if (context.server.isConnected())
            throw new Exception("Runtime error: DataServer already running");

        // Calculate complex expressions
        int port = (int) Calculator.calculate(args[0]);
        int linesPerSecond = (int) Calculator.calculate(args[1]);

        // Creating 'data server' and run it in a different thread
        Server dataServer = context.server;
        dataServer.open(port, new DataClientHandler(linesPerSecond), context);

        Thread.sleep(2000); // For synchronization with test system
    }
}

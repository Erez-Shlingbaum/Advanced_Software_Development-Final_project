package Model.Interpreter.Commands;

import Model.Interpreter.Expressions.Calculator;
import Model.Interpreter.Interpeter.InterpreterContext;

public class ConnectCommand implements Command
{

    @Override
    public void execute(String[] args, InterpreterContext context) throws Exception
    {
        //check exceptions
        if (args.length != 2)
            throw new Exception("Syntax error: Connect expects two arguments");

        String ipAddress = args[0];
        int port = (int) Calculator.calculate(args[1]); //calculate complex expressions //IDEA: port must to be an integer, check if the answer incorrect

        if (context.client.isConnected())
            throw new Exception("Error: trying to 'connect' twice");

        context.client.connect(ipAddress, port);//might throw exception if ip not valid
    }
}

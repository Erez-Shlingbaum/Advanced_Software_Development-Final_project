package Model.Interpreter.Commands;

import Model.Interpreter.Client_Side.ConnectClient;
import Model.Interpreter.Expressions.Calculator;

public class ConnectCommand implements Command
{

	@Override
	public void execute(String[] args) throws Exception
	{
		//check exceptions
		if(args.length != 2)
			throw new Exception("Syntax error: Connect expects two arguments");

		String ipAddress = args[0];
		int port = (int)Calculator.calculate(args[1]); //calculate complex expressions //IDEA: port must to be an integer, check if the answer incorrect

		System.out.println("ipAddress = "+ ipAddress);
		System.out.println("port = "+ port);

		if(ConnectClient.isReferenceExists())
			throw new Exception("Error: trying to 'connect' twice");
		ConnectClient.getReference().connect(ipAddress, port);//might throw exception if ip not valid
	}
}

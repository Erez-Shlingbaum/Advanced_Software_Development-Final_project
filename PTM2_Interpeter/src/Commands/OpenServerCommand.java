package Commands;


import Server_Side.DataClientHandler;
import Server_Side.DataServer;

public class OpenServerCommand implements Command
{

	@Override
	public void execute(String[] args)
	{
		System.out.println("Open executed!");

		//TODO: check syntax errors
		DataServer dataServer = DataServer.getReference();	//TODO: check error with 'isReferenceExists'

		dataServer.open(Integer.parseInt(args[0]), new DataClientHandler(Integer.parseInt(args[1])));//runs in a different thread
	}
}

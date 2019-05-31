package Commands;

import Client_Side.ConnectClient;

public class DisconnectCommand implements Command
{
	@Override
	public void execute(String[] args) throws Exception
	{
		ConnectClient.getReference().sendMessage("bye");
	}
}

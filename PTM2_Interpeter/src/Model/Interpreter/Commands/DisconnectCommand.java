package Model.Interpreter.Commands;

import Model.Interpreter.Client_Side.ConnectClient;

public class DisconnectCommand implements Command
{
    @Override
    public void execute(String[] args) throws Exception
    {
        ConnectClient.getReference().sendMessage("bye");
    }
}

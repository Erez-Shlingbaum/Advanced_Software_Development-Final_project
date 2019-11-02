package Model.Interpreter.Commands;

import Model.Interpreter.Interpeter.InterpreterContext;

public class DisconnectCommand implements Command
{
    @Override
    public void execute(String[] args, InterpreterContext context) throws Exception
    {
        context.client.sendMessage("bye");
    }
}

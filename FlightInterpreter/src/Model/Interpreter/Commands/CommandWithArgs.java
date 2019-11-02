package Model.Interpreter.Commands;

import Model.Interpreter.Interpeter.InterpreterContext;

public class CommandWithArgs
{
    public final Command command;
    private final String[] args;
    private final InterpreterContext context;

    public CommandWithArgs(Command command, String[] args, InterpreterContext context)
    {
        this.command = command;
        this.args = args;
        this.context = context;
    }

    public void executeWithArgs() throws Exception
    {
        this.command.execute(this.args, this.context);
    }
}

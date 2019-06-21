package Model.Interpreter.Commands;

public class CommandWithArgs
{
    public final Command command;
    private final String[] args;

    public CommandWithArgs(Command command, String[] args)
    {
        this.command = command;
        this.args = args;
    }

    public void executeWithArgs() throws Exception
    {
        this.command.execute(this.args);
    }
}

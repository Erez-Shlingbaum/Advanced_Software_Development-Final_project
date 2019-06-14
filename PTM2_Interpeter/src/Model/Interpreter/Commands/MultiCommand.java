package Model.Interpreter.Commands;

import java.util.List;

public abstract class MultiCommand implements Command
{
    protected List<CommandWithArgs> commandsToExecute;

    public void setToExecute(List<CommandWithArgs> toExecute)
    {
        this.commandsToExecute = toExecute;
    }
}

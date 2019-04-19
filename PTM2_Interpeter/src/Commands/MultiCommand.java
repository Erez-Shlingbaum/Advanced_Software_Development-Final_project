package Commands;

import java.util.List;

public abstract class MultiCommand implements Command
{
    protected List<CommandWithArgs> toExecute;

    public void setToExecute(List<CommandWithArgs> toExecute)
    {
        this.toExecute = toExecute;
    }
}

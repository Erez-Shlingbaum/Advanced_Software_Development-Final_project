package Model.Interpreter.Commands;

public interface Command
{
    void execute(String[] args) throws Exception;
}

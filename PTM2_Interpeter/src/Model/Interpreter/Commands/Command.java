package Model.Interpreter.Commands;

import Model.Interpreter.Interpeter.InterpreterContext;

public interface Command
{
    void execute(String[] args, InterpreterContext context) throws Exception;
}

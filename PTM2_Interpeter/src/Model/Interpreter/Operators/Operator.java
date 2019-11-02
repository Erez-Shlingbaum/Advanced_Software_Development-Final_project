package Model.Interpreter.Operators;

import Model.Interpreter.Interpeter.InterpreterContext;

interface Operator
{
    void execute(String[] args, InterpreterContext context) throws Exception;
}

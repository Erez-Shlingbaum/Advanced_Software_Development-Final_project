package Model.Interpreter.Commands;

import Model.Interpreter.Expressions.Calculator;
import Model.Interpreter.Expressions.PreCalculator;
import Model.Interpreter.Interpeter.InterpreterContext;

public class SleepCommand implements Command
{

    @Override
    public void execute(String[] args, InterpreterContext context) throws Exception
    {
        if (args.length == 0)
            throw new Exception("Syntax error: sleep expects 'sleep expression'");

        String expression = PreCalculator.connectWords(args);
        expression = PreCalculator.replaceVarNames(expression, context);
        int result = (int) Calculator.calculate(expression);

        Thread.sleep(result);
    }
}

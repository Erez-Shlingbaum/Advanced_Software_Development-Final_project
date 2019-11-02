package Model.Interpreter.Commands;

import Model.Interpreter.Expressions.Calculator;
import Model.Interpreter.Expressions.PreCalculator;

public class ReturnCommand implements Command
{
    public static double value;

    @Override
    public void execute(String[] args) throws Exception
    {
        Thread.sleep(500); // for synchronization issues with Model.Interpreter.test system
        String expresion = PreCalculator.replaceVarNames(String.join("", args).trim());
        value = Calculator.calculate(expresion);
    }
}

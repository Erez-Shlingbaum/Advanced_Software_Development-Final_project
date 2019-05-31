package Commands;

import Expressions.Calculator;
import Expressions.PreCalculator;

public class ReturnCommand implements Command
{
	public static double value;
	@Override
	public void execute(String[] args) throws Exception
	{
		String expresion = PreCalculator.replaceVarNames(String.join("", args).trim());
		value = Calculator.calculate(expresion);
	}
}

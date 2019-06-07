package Commands;

import Expressions.Calculator;
import Expressions.PreCalculator;

public class ReturnCommand implements Command
{
	public static double value;
	@Override
	public void execute(String[] args) throws Exception
	{
		Thread.sleep(500); // for synchronization issues with test system
		String expresion = PreCalculator.replaceVarNames(String.join("", args).trim());
		value = Calculator.calculate(expresion);
	}
}

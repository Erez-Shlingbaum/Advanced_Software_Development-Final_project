package Model.Commands;

import Model.Expressions.Calculator;
import Model.Expressions.PreCalculator;

public class ReturnCommand implements Command
{
	public static double value;
	@Override
	public void execute(String[] args) throws Exception
	{
		Thread.sleep(500); // for synchronization issues with Model.test system
		String expresion = PreCalculator.replaceVarNames(String.join("", args).trim());
		value = Calculator.calculate(expresion);
	}
}

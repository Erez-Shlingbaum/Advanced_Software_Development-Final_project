package Model.Commands;

import Model.Expressions.Calculator;
import Model.Expressions.PreCalculator;

public class SleepCommand implements Command
{

	@Override
	public void execute(String[] args) throws Exception
	{
		if(args.length == 0)
			throw new Exception("Syntax error: sleep expects 'sleep expression'");

		String expression = PreCalculator.connectWords(args);
		expression = PreCalculator.replaceVarNames(expression);
		int result = (int)Calculator.calculate(expression);

		Thread.sleep(result);
	}
}

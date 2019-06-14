package Model.Interpreter.Commands;

import Model.Interpreter.Expressions.Calculator;
import Model.Interpreter.Expressions.PreCalculator;

public class PrintCommand implements Command
{

	@Override
	public void execute(String[] args) throws Exception
	{
		if (args.length == 0)
			throw new Exception("Syntax error: print expects string \"\" or expression");
		if (args[0].charAt(0) == '"' && args[args.length - 1].charAt(args[args.length - 1].length() - 1) == '"')//check if first character is " and last is "
		{
			StringBuilder stringBuilder = new StringBuilder();
			for (String word : args)
				stringBuilder.append(word).append(' ');
			System.out.println(stringBuilder.substring(1, stringBuilder.length() - 2));//ignore " and 1 space
		} else    //if we have a complex expression
		{
			String expression = PreCalculator.connectWords(args);
			expression = PreCalculator.replaceVarNames(expression);
			double result = Calculator.calculate(expression);
			System.out.println(result);
		}

	}
}
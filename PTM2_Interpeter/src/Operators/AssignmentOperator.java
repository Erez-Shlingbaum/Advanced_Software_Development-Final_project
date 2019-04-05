package Operators;

import Commands.DefineVarCommand;
import Expressions.Calculator;
import Interpeter.Variable;

import java.util.Arrays;

public class AssignmentOperator implements Operator
{
	@Override
	public void execute(String[] args) throws Exception //args will contain all of the line, including variable name
	{
		System.out.println("Assingment operator executed!");//TODO: delete this
		if (args.length < 3 || !args[1].equals("="))
			throw new Exception("syntax error: expecting 'varName = bind \"path\"' or 'varName = expression'");

		String varName = args[0];

		if (args.length == 4 && args[2].equals("bind"))//varName = bind "path"
		{
			if (!args[2].equals("bind"))
				throw new Exception("Syntax error: assingment operator expects 'bind'");
			Variable var = DefineVarCommand.getSymbolTable().get(varName);
			var.setPath(args[3]);
			//DefineVarCommand.getSymbolTable().put(args[0], var);
		}
		else
		{
			Variable var = DefineVarCommand.getSymbolTable().get(varName);
			String expression = Calculator.connectWords(Arrays.copyOfRange(args, 2, args.length));
			var.setValue(Calculator.calculate(expression), true);
			//DefineVarCommand.getSymbolTable().put(args[0], new Variable(args[2]))
		}
	}
}
package Operators;

import Commands.DefineVarCommand;
import Expressions.Calculator;
import Expressions.PreCalculator;
import Interpeter.Variable;

import java.util.Arrays;

public class AssignmentOperator implements Operator
{
	@Override
	public void execute(String[] args) throws Exception //args will contain all of the line, including variable name
	{
		if (args.length < 3 || !args[1].equals("="))
			throw new Exception("syntax error: expecting 'varName = bind \"path\"' or 'varName = expression'");

		String varName = args[0];

		if (args.length == 4 && args[2].equals("bind"))//varName = bind "path"
		{            //if (!args[2].equals("bind"))

			//	throw new Exception("Syntax error: assingment operator expects 'bind'");
			Variable var = DefineVarCommand.getSymbolTable().get(varName);
			var.setPath(args[3]);
			//DefineVarCommand.getSymbolTable().put(args[0], var);
		} else //varName = expression
		{
			Variable var = DefineVarCommand.getSymbolTable().get(varName);
			//remove whitespace from expression
			String expression = PreCalculator.connectWords(Arrays.copyOfRange(args, 2, args.length));
			//replace varNames with their value
			expression = PreCalculator.replaceVarNames(expression);
			//set value in symbol table and notify server with new value
			if (var.getPath() != null)//check if variables is binded or not, and notify only if binded
				var.setValue(Calculator.calculate(expression), true);
			else
				var.setValue(Calculator.calculate(expression), false);
		}
	}
}
package Model.Interpreter.Operators;

import Model.Interpreter.Commands.DefineVarCommand;
import Model.Interpreter.Expressions.Calculator;
import Model.Interpreter.Expressions.PreCalculator;
import Model.Interpreter.Interpeter.Variable;

import java.util.Arrays;

public class AssignmentOperator implements Operator
{
	@Override
	public void execute(String[] args) throws Exception //args will contain all of the line, including variable name
	{
		//if (args.length < 3 || !args[1].equals("="))
		//	throw new Exception("syntax error: expecting 'varName = bind \"path\"' or 'varName = expression'");

		String varName = args[0];
		if (String.join(" ", args).contains("bind"))//varName = bind "path"
		{
			String path = args[3];
            for (Variable variable : DefineVarCommand.getSymbolTable().values()) // has someone already bounded to path?
			{
				if (path.equals(variable.getPath())) // if yes, the use his variable
				{
					DefineVarCommand.getSymbolTable().put(varName, variable);
					return;
				}
			}

			Variable var = DefineVarCommand.getSymbolTable().get(varName);
			var.setPath(path);
			//DefineVarCommand.getSymbolTable().put(args[0], var);
		} else //varName = expression
		{
			Variable var = DefineVarCommand.getSymbolTable().get(varName);
			//remove whitespace from expression
			String expression = PreCalculator.connectWords(Arrays.copyOfRange(args, 2, args.length));
			//replace varNames with their value
			expression = PreCalculator.replaceVarNames(expression);
			//set value in symbol table and notify server with new value
            if (var.getPath() != null)//check if variables is bounded or not, and notify only if bounded
				var.setValue(Calculator.calculate(expression), true);
			else
				var.setValue(Calculator.calculate(expression), false);
		}
	}
}
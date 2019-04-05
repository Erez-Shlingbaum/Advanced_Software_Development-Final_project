package Interpeter;

import Commands.Command;
import Commands.DefineVarCommand;
import Operators.AssignmentOperator;

import java.util.Arrays;
import java.util.HashMap;

public class Parser
{
	public static void parser(String[] tokens, HashMap<String, Command> keywords) throws Exception
	{
		if (tokens.length == 0)
			return;

		String cmd = tokens[0];

		if (keywords.containsKey(cmd))    //first token is the command
			keywords.get(cmd).execute(Arrays.copyOfRange(tokens, 1, tokens.length));
		else if (DefineVarCommand.getSymbolTable().containsKey(cmd))//if token is variable
		{
			new AssignmentOperator().execute(tokens);//varName = bind "path" or expression
		}
		else
			throw new Exception("Syntax error: command not found");
		//TODO: think if curly braces {} should be command or maybe how we should handle them
	}
}
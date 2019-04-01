package interpeter;

import Commands.Command;
import Commands.DefineVarCommand;

import java.util.Arrays;
import java.util.HashMap;

public class Parser
{
	public static void parser(String[] tokens, HashMap<String, Command> keywords) throws Exception
	{
		if(tokens.length == 0)
			return;

		if(keywords.containsKey(tokens[0]))	//first token is the command
			keywords.get(tokens[0]).execute(Arrays.copyOfRange(tokens, 1, tokens.length));
		else if(DefineVarCommand.getSymbolTable().containsKey(tokens[0]))//if token is variable
		{
			DefineVarCommand.getSymbolTable().put(tokens[0], tokens[3]);	//TODO: make better names instead of tokens[3]
		}
		else throw new Exception("Syntax error: command not found");
		//TODO: think if curly braces {} should be command or maybe how we should handle them
	}
}
package interpeter;

import Commands.Command;

import java.util.Arrays;
import java.util.HashMap;

public class Parser
{
	public static void parser(String[] tokens, HashMap<String, Command> keywords) throws Exception
	{
		//TODO IF TOKENS.SIZE > 0 or do nothing
		//TODO check sizeof tokens and errors
		if(keywords.containsKey(tokens[0]))	//first token is the command
			keywords.get(tokens[0]).execute(Arrays.copyOfRange(tokens, 1, tokens.length));
		else //TODO: check if token is a variable (saved in symbol table)
		{

		}// else -> syntax error
		//TODO: think if curly braces {} should be command or maybe how we should handle them
	}
}
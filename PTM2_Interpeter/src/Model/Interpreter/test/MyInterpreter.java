package Model.Interpreter.test;

import Model.Interpreter.Client_Side.ConnectClient;
import Model.Interpreter.Commands.*;
import Model.Interpreter.Interpeter.Lexer;
import Model.Interpreter.Interpeter.Parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class MyInterpreter
{

	// one shared(for different instances of this class) keywords map
	static HashMap<String, Command> keywords;

	public int interpret(String[] lines)
	{
		//variable declaration
		String line;
		String[] tokens;
		List<String> tokenList = new LinkedList<>();
		boolean isMultiCommand = false;

		// define our language syntax
		if (this.keywords == null)
			this.initializeInterpreter();

		Scanner scanner = new Scanner(String.join("\n", lines));
		//System.out.print("> ");

		do
		{
			try
			{
				line = scanner.nextLine();
				tokens = Lexer.lexer(line);
				//if there is a complex command (loop, if), put all the commands until "}" in an array
				if (String.join(" ", tokens).contains("{"))
				{
					isMultiCommand = true;
					//tokens = Arrays.copyOfRange(tokens, 0, tokens.length);
					do
					{
						if (tokens.length != 0 && (tokens[0].charAt(0) != '/' || tokens[0].charAt(1) != '/')) // if line is not empty and is not comment then add it
						{
							for (String token : tokens)//add every token into a list
								tokenList.add(token);
							tokenList.add("\n");//to differentiate different commands - later in parser
						}
						line = scanner.nextLine();
						tokens = Lexer.lexer(line);
					} while (!tokens[0].equals("}")); //continue reading lines until }

					//convert list back to String[]
					tokens = tokenList.toArray(new String[0]);//toArray will automatically allocate space, but we still need to send an array
				}

				// sending tokens to parser
				Parser.parser(tokens, keywords, isMultiCommand);
				isMultiCommand = false;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			//System.out.print("> ");    //might be a bug here

		} while (scanner.hasNextLine());
		cleanupInterpreter();
		return (int) ReturnCommand.value;
	}

	public void setVariableInSimulator(String path, double val)
	{
		try
		{
			ConnectClient.getReference().sendMessage("set " + path + " " + val);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initializeInterpreter()
	{
		keywords = new HashMap<>();
		keywords.put("openDataServer", new OpenServerCommand());
		keywords.put("connect", new ConnectCommand());
		keywords.put("var", new DefineVarCommand());
		keywords.put("print", new PrintCommand());
		keywords.put("sleep", new SleepCommand());
		keywords.put("while", new WhileCommand());
		keywords.put("return", new ReturnCommand());
		keywords.put("disconnect", new DisconnectCommand());
		keywords.put("pause", new PauseCommand());
	}

	private static void cleanupInterpreter()
	{
		// for GUI purposes we do not want to clear the current interpreter state

		//ConnectClient.cleanUpReference();
		//if (DataServer.isReferenceExists())
		//	DataServer.getReference().close();
		//DefineVarCommand.getSymbolTable().clear();
	}
}

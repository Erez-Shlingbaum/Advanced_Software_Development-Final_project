package test;

import Commands.*;
import Interpeter.Lexer;
import Interpeter.Parser;
import Server_Side.DataServer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class MyInterpreter {

	public static  int interpret(String[] lines){
		//variable declaration
		HashMap<String, Command> keywords = new HashMap<>();
		String line;
		String[] tokens;
		List<String> tokenList = new LinkedList<>();
		boolean isMultiCommand = false;

		// define our language syntax
		keywords.put("openDataServer", new OpenServerCommand());
		keywords.put("connect", new ConnectCommand());
		keywords.put("var", new DefineVarCommand());
		keywords.put("print", new PrintCommand());
		keywords.put("sleep", new SleepCommand());
		keywords.put("while", new WhileCommand());
		keywords.put("return", new ReturnCommand());
		keywords.put("disconnect", new DisconnectCommand());

		Scanner scanner = new Scanner(String.join("\n", lines));
		//System.out.print("> ");    //might be a bug here

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
						for (String token : tokens)//add every token into a list
							tokenList.add(token);
						tokenList.add("\n");//to differentiate different commands - later in parser

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
				System.out.println(e.getMessage());
				System.out.println();
				e.printStackTrace();
			}
			//System.out.print("> ");    //might be a bug here

		} while (scanner.hasNextLine());
		cleanup();
		return (int)ReturnCommand.value;
	}
	private static void cleanup()
	{
		if(DataServer.isReferenceExists())
			DataServer.getReference().close();
		DefineVarCommand.getSymbolTable().clear();
	}
}

package interpeter;

import Commands.Command;
import Commands.OpenServerCommand;
import Server_Side.DataServer;

import java.util.HashMap;
import java.util.Scanner;

public class Main
{
	//TODO: open script from file, given in args
	public static void main(String[] args)
	{
		HashMap<String, Command> keywords = new HashMap<>();

		keywords.put("openDataServer", new OpenServerCommand());
		//keywords.put("connect", new ConnectCommand());
		//keywords.put("var", new DefineVarCommand());


		Scanner scanner = new Scanner(System.in);
		System.out.print("> ");    //might be a bug here

		do
		{
			Parser.parser(Lexer.lexer(scanner.nextLine()),keywords);
			System.out.print("> ");    //might be a bug here

		} while (scanner.hasNextLine());


		//when finished
		//TODO: chekc if server even exists with 'isReferenceExists'
		DataServer.getReference().close();
	}
}

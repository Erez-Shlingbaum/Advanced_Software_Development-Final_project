package Interpeter;

import Commands.Command;
import Commands.ConnectCommand;
import Commands.DefineVarCommand;
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
        keywords.put("connect", new ConnectCommand());
        keywords.put("var", new DefineVarCommand());


        Scanner scanner = new Scanner(System.in);
        System.out.print("> ");    //might be a bug here

        do
        {
            try
            {
                //Lexing the next line and sending it to parser
                Parser.parser(Lexer.lexer(scanner.nextLine()), keywords);
            } catch (Exception e)
            {
                System.out.println(e.getMessage());
                System.out.println();
                System.out.println("Stack trace for debug purposes: :):):):):):):):)");
                e.printStackTrace();    //TODO: check if we should close the program
            }
            System.out.print("> ");    //might be a bug here

        } while (scanner.hasNextLine());


        //when finished
        //TODO: check if server even exists with 'isReferenceExists'
        DataServer.getReference().close();

    }
}
/**
 * Some thoughts:
 * only after a while()
 * or			  if()
 * there can be curly braces {} {} , so our code has to check that option(current line or next line)
 */
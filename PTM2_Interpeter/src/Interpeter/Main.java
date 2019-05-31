package Interpeter;

import Commands.*;
import Server_Side.DataServer;

import java.util.*;

public class Main
{
    public static void main(String[] args)
    {
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

        Scanner scanner = new Scanner(System.in);
        System.out.print("> ");    //might be a bug here

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
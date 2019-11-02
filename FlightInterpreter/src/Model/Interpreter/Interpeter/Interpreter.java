package Model.Interpreter.Interpeter;

import Model.Interpreter.Commands.ReturnCommand;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Interpreter
{
    private final InterpreterContext interpreterContext;
    private final Parser parser;

    public Interpreter(InterpreterContext interpreterContext)
    {
        this.interpreterContext = interpreterContext;
        parser = new Parser(interpreterContext);
    }

    public void clearSymboltable()
    {
        interpreterContext.clearSymbolTable();
    }

    public int interpret(String[] lines)
    {
        boolean isMultiCommand = false;

        Scanner scanner = new Scanner(String.join("\n", lines));
        do
        {
            try
            {
                String line = scanner.nextLine();
                String[] tokens = Lexer.lexString(line);

                // If there is a complex command (loop, if), put all the commands until "}" in an array
                if (String.join(" ", tokens).contains("{"))
                {
                    isMultiCommand = true;
                    List<String> tokenList = new LinkedList<>();
                    do
                    {
                        if (tokens.length != 0 && (tokens[0].charAt(0) != '/' || tokens[0].charAt(1) != '/')) // If line is not empty and is not comment then add it
                        {
                            // Add every token into a list
                            tokenList.addAll(Arrays.asList(tokens));
                            tokenList.add("\n"); // To differentiate different commands later in the parser
                        }
                        line = scanner.nextLine();
                        tokens = Lexer.lexString(line);
                    } while (!tokens[0].equals("}")); // Continue reading lines until reaching '}'

                    // Convert list back to String[]
                    tokens = tokenList.toArray(new String[0]); // toArray will automatically allocate space, but we still need to send an array
                }

                // Send tokens to parser
                parser.parseTokens(tokens, isMultiCommand);
                isMultiCommand = false;
            } catch (InterruptedException e)
            {
                return (int) ReturnCommand.value;    // just stop execution.
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        } while (scanner.hasNextLine());
        return (int) ReturnCommand.value;
    }

    public void setVariableInSimulator(String path, double val)
    {
        try
        {
            interpreterContext.client.sendMessage("set " + path + " " + val);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isConnectedToSimulator()
    {
        return interpreterContext.client.isConnected();
    }

    public double getVariableFromScript(String variableName)
    {
        if (interpreterContext.symbolTable.containsKey(variableName))
            return interpreterContext.symbolTable.get(variableName).getValue();
        return 0; //some random number
    }
}

package Model.Interpreter.Interpeter;

import Model.Interpreter.Commands.Command;
import Model.Interpreter.Commands.CommandWithArgs;
import Model.Interpreter.Commands.DefineVarCommand;
import Model.Interpreter.Commands.MultiCommand;
import Model.Interpreter.Operators.AssignmentOperator;

import java.util.*;

public class Parser
{

    public static void parser(String[] tokens, HashMap<String, Command> keywords, boolean isMultiCommand) throws Exception
    {
        if (tokens.length == 0)
            return;
        if(tokens[0].charAt(0) == '/' && tokens[0].charAt(1) == '/') // we can now create comments
            return;

        if (isMultiCommand)
        {
            List<CommandWithArgs> commandWithArgsList = new LinkedList<>();
            CommandWithArgs multiCommand;
            int start = 0, end;
            end = getEndOfLine(tokens, start); //find \n
            //the first Command is while/ if...
            multiCommand = generateCommand(Arrays.copyOfRange(tokens, start, end), keywords);
            for (start = end + 1; start < tokens.length - 1 && end < tokens.length - 1; start = end+1)
            {
                end = getEndOfLine(tokens, start);//end will index '\n'
                commandWithArgsList.add(generateCommand(Arrays.copyOfRange(tokens, start, end), keywords));
            }
            ((MultiCommand) multiCommand.command).setToExecute(commandWithArgsList);
            multiCommand.executeWithArgs();
        } else
            generateCommand(tokens, keywords).executeWithArgs();

    }

    private static int getEndOfLine(String[] tokens, int start)
    {
        int i = start;
        for (; i < tokens.length && !tokens[i].equals("\n"); i++) ;
        return i;
    }

    private static CommandWithArgs generateCommand(String[] tokens, HashMap<String, Command> keywords) throws Exception//should maybe return pair(cmd, args)
    {
        Command command;
        String[] args;

        String cmd = tokens[0];
        if (keywords.containsKey(cmd))    //first token is the command
        {
            command = keywords.get(cmd);
            args = Arrays.copyOfRange(tokens, 1, tokens.length);
        } else// if (DefineVarCommand.getSymbolTable().containsKey(cmd))//if token is variable
        {
            Scanner scanner = new Scanner(String.join(" ", tokens));
            scanner.useDelimiter("=");
            String varName = scanner.next().trim();
            scanner.useDelimiter("");
            scanner.next("=");

            List<String> expression;
            if(String.join(" ", tokens).contains("bind"))
            {
                scanner.useDelimiter(" ");
                expression = new ArrayList<>(Arrays.asList(scanner.next().trim(), scanner.next().trim())); // "bind", "path"
            }
            else
                expression = new ArrayList<>(Collections.singletonList(scanner.nextLine().trim()));//.toArray(new String[0]); // this is some crazy code

            if(!DefineVarCommand.getSymbolTable().containsKey(varName))
                throw new Exception("Syntax error: variable not found");

            command = (arguments) -> new AssignmentOperator().execute(arguments);//varName = bind "path" or expression
            expression.add(0, "=");
            expression.add(0, varName);
            args = expression.toArray(new String[0]);// varName, =, expression
        }

        return new CommandWithArgs(command, args);
    }
}
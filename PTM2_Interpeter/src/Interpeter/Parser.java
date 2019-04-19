package Interpeter;

import Commands.Command;
import Commands.CommandWithArgs;
import Commands.DefineVarCommand;
import Commands.MultiCommand;
import Operators.AssignmentOperator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Parser
{

    public static void parser(String[] tokens, HashMap<String, Command> keywords, boolean isMultiCommand) throws Exception
    {
        if (tokens.length == 0)
            return;

        if (isMultiCommand)
        {
            List<CommandWithArgs> commandWithArgsList = new LinkedList<>();
            CommandWithArgs multiCommand;
            int start = 0, end = 0;
            end = getEndOfLine(tokens, start);
            //the first Command is while/ if...
            multiCommand = generateCommand(Arrays.copyOfRange(tokens, start, end), keywords);
            for (start = end; start < tokens.length - 1 && end < tokens.length - 1; start = end)//TODO change to length -1 if not works
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
        return i + 1;//return index 1 after '\n'
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
        } else if (DefineVarCommand.getSymbolTable().containsKey(cmd))//if token is variable
        {
            command = (arguments) -> new AssignmentOperator().execute(arguments);//varName = bind "path" or expression
            args = tokens;
        } else
            throw new Exception("Syntax error: command not found");

        return new CommandWithArgs(command, args);
    }
}
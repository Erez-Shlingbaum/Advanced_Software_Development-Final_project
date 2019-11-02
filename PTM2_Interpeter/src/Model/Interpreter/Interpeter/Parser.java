package Model.Interpreter.Interpeter;

import Model.Interpreter.Commands.Command;
import Model.Interpreter.Commands.CommandWithArgs;
import Model.Interpreter.Commands.MultiCommand;
import Model.Interpreter.Operators.AssignmentOperator;

import java.util.*;

public class Parser
{
    private InterpreterContext context;

    public Parser(InterpreterContext context)
    {
        this.context = context;
    }

    public void parseTokens(String[] tokens, boolean isMultiCommand) throws Exception
    {
        if (tokens.length == 0)
            return;
        if (tokens[0].charAt(0) == '/' && tokens[0].charAt(1) == '/') // Support for comments
            return;

        if (!isMultiCommand)
            generateCommand(tokens, context.keywords).executeWithArgs();
        else
        {
            int start = 0;
            int end = getEndOfLine(tokens, start); //find \n

            // Find multicommand (e.g if, while..)
            CommandWithArgs multiCommand = generateCommand(Arrays.copyOfRange(tokens, start, end), context.keywords);

            // Find sub commands
            List<CommandWithArgs> commandWithArgsList = new LinkedList<>();
            for (start = end + 1; start < tokens.length - 1 && end < tokens.length - 1; start = end + 1)
            {
                end = getEndOfLine(tokens, start);//end will index '\n'
                commandWithArgsList.add(generateCommand(Arrays.copyOfRange(tokens, start, end), context.keywords));
            }

            // Execute multi command
            ((MultiCommand) multiCommand.command).setToExecute(commandWithArgsList);
            multiCommand.executeWithArgs();
        }
    }

    private int getEndOfLine(String[] tokens, int start)
    {
        int i = start;
        for (; i < tokens.length && !tokens[i].equals("\n"); i++) ;
        return i;
    }

    private CommandWithArgs generateCommand(String[] tokens, HashMap<String, Command> keywords) throws Exception//should maybe return pair(cmd, args)
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
            if (String.join(" ", tokens).contains("bind"))
            {
                scanner.useDelimiter(" ");
                expression = new ArrayList<>(Arrays.asList(scanner.next().trim(), scanner.next().trim())); // "bind", "path"
            } else
                expression = new ArrayList<>(Collections.singletonList(scanner.nextLine().trim()));

            if (!context.symbolTable.containsKey(varName))
                throw new Exception("Syntax error: variable not found");

            command = (arguments, context) -> new AssignmentOperator().execute(arguments, context); // varName = bind "path" or expression
            expression.add(0, "=");
            expression.add(0, varName);
            args = expression.toArray(new String[0]); // varName, =, expression
        }

        return new CommandWithArgs(command, args, context);
    }
}
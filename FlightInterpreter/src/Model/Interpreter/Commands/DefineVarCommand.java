package Model.Interpreter.Commands;

import Model.Interpreter.Interpeter.InterpreterContext;
import Model.Interpreter.Interpeter.Variable;
import Model.Interpreter.Operators.AssignmentOperator;

import java.util.*;

public class DefineVarCommand implements Command
{
    @Override
    public void execute(String[] args, InterpreterContext context) throws Exception
    {
        String varName;
        if (String.join("", args).contains("="))
        {
            Scanner scanner = new Scanner(String.join(" ", args));
            scanner.useDelimiter("=");
            varName = scanner.next().trim();
            scanner.useDelimiter("");
            scanner.next("=");

            List<String> expression;
            if (String.join(" ", args).contains("bind"))
            {
                scanner.useDelimiter(" ");
                expression = new ArrayList<>(Arrays.asList(scanner.next().trim(), scanner.next().trim())); // "bind", "path"
            } else
                expression = new ArrayList<>(Collections.singletonList(scanner.nextLine().trim()));

            expression.add(0, "=");
            expression.add(0, varName);
            args = expression.toArray(new String[0]);// varName, =, expression

        } else
            varName = args[0];

        //check more exceptions
        if (context.symbolTable.containsKey(varName))
            throw new Exception("Error: trying to define variable that already exists in symbolTable");
        context.symbolTable.put(varName, new Variable(context));

        if (args.length >= 2)
            if (args[1].equals("="))
                new AssignmentOperator().execute(args, context);
            else throw new Exception("Syntax error: var expects '='");
    }
}
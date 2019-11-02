package Model.Interpreter.Operators;

import Model.Interpreter.Expressions.Calculator;
import Model.Interpreter.Expressions.PreCalculator;
import Model.Interpreter.Interpeter.InterpreterContext;
import Model.Interpreter.Interpeter.Variable;

import java.util.Arrays;

public class AssignmentOperator implements Operator
{
    @Override
    public void execute(String[] args, InterpreterContext context) throws Exception // Args will contain all of the line, including variable name
    {
        String varName = args[0];
        if (String.join(" ", args).contains("bind")) // varName = bind "path"
        {
            String path = args[3];
            for (Variable variable : context.symbolTable.values()) // Has someone already bounded to path?
                if (path.equals(variable.getPath())) // If yes, the use his variable
                {
                    context.symbolTable.put(varName, variable);
                    return;
                }

            Variable var = context.symbolTable.get(varName);
            var.setPath(path);
        } else // varName = expression
        {
            Variable var = context.symbolTable.get(varName);

            // Remove whitespace from expression
            String expression = PreCalculator.connectWords(Arrays.copyOfRange(args, 2, args.length));

            // Replace varNames with their value
            expression = PreCalculator.replaceVarNames(expression, context);

            // Set value in symbol table and notify server with new value
            if (var.getPath() != null) // Check if variables is bounded or not, and notify only if bounded
                var.setValue(Calculator.calculate(expression), true);
            else
                var.setValue(Calculator.calculate(expression), false);
        }
    }
}
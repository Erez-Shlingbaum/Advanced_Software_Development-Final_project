package Model.Interpreter.Commands;

import Model.Interpreter.Expressions.Calculator;
import Model.Interpreter.Expressions.PreCalculator;
import Model.Interpreter.Interpeter.InterpreterContext;

import java.util.Scanner;

public class WhileCommand extends MultiCommand
{
    @Override
    public void execute(String[] args, InterpreterContext context) throws Exception
    {
        // join args array into a connected string(for scanner)
        String line = String.join("", args);
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter("<");
        String leftExpression = scanner.next();

        // ignore non expression..
        scanner.useDelimiter("");
        scanner.next("<");

        scanner.useDelimiter("\\{");
        String rightExpression = scanner.next();

        leftExpression = leftExpression.trim();
        rightExpression = rightExpression.trim();

        while (Calculator.calculate(PreCalculator.replaceVarNames(leftExpression, context)) < Calculator.calculate(PreCalculator.replaceVarNames(rightExpression, context)))
            for (CommandWithArgs cmd : super.commandsToExecute)
                cmd.executeWithArgs();
    }
}

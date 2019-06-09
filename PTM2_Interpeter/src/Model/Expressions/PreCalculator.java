package Model.Expressions;

import Model.Commands.DefineVarCommand;

import java.util.LinkedList;
import java.util.List;

public class PreCalculator
{

    // "5 + 3 / 800 - var" -> "5+3/800-var"
    public static String connectWords(String[] expression)
    {
        return String.join("", expression).replace(" ", "").trim();
    }


    //replace variable names with their value
    public static String replaceVarNames(String expression) throws Exception
    {
        //variable declaration
        int startIndex = 0;
        int endIndex = 0;
        List<String> variableNames = new LinkedList<>();

        // before replacing var names with their values, we make changes to occurrences of '-' as a prefix
        expression = replaceNegativePrefix(expression);

        //iterate on the expression and find variable names while ignoring Model.Operators or '()'
        while (startIndex < expression.length() && endIndex < expression.length())
        {
            //while not character - startIndex++
            while (startIndex < expression.length() && !isEndlishLetter(expression.charAt(startIndex)))
                startIndex++;
            if (startIndex >= expression.length())//if there are no variables in expression, then don't do anything
                if (variableNames.size() == 0)
                    return expression;
                else break;

            endIndex = startIndex;

            //while is character or number - endIndex++
            while (endIndex < expression.length() && (isEndlishLetter(expression.charAt(endIndex)) || Character.isDigit(expression.charAt(endIndex))))
                endIndex++;
            String varName = expression.substring(startIndex, endIndex);

            if (!DefineVarCommand.getSymbolTable().containsKey(varName))
                throw new Exception("Syntax error: variable name not found: " + varName);

            //saving all variable names in expression into a list, to replace them later
            variableNames.add(varName);
            startIndex = endIndex;
        }

        for (String varName : variableNames)
        {
            double varValue = DefineVarCommand.getSymbolTable().get(varName).getValue();
            expression = expression.replace(varName, Double.toString(varValue));
        }
        variableNames.clear();
        return expression;
    }


    // replace a negative number x (x<0) with "0x" so that when x is replaced
    // with its value that expression will resolve to "0-x",
    // also replaces negative PREFIX such as -x with "0-x" if x>0 and with "0x" if x>0
    private static String replaceNegativePrefix(String expression)
    {
        expression = expression.trim();
        String varName = null;

        // if we have a - prefix to this expression (this is a private case of the unary '-' operator.)
        if (expression.charAt(0) == '-')
        {
            // check if there is a variable that this '-' is for (we assume that this case only happens for the first variable)
            for (int endIndex = 1; endIndex < expression.length(); endIndex++)
            {
                char letter = expression.charAt(endIndex);

                // if current letter is not an english letter and not a digit, OR we arrived to end of this expression
                if ((!isEndlishLetter(letter) && !isDigit(letter)) || endIndex == expression.length() - 1)
                {
                    // if the reason we entered this if is because we got to the end of this expression then increase endIndex. (substring is exclusive with endIndex)
                    if (endIndex == expression.length() - 1)
                        endIndex++;
                    varName = expression.substring(1, endIndex);
                    break;
                }
            }
            // check if this '-' is for a number
            if (DefineVarCommand.getSymbolTable().get(varName) == null)
                return String.join("", "0", expression);

            // if this variable is negative then we replace "-var" with "(0-(0var))"
            // because when var is replaced with its value then a minus sign will be added
            // if this variable is positive then we replave "-var" with "(0-var)"
            if (DefineVarCommand.getSymbolTable().get(varName).getValue() < 0)
                expression = expression.replace("-" + varName, "(0-(0" + varName + "))"); // NOTICE THIS IS NOW CHANGED AND CORRECT
            else
                expression = expression.replace("-" + varName, "(0-" + varName + ")");
        } else // if we dont have a '-' prefix
        {
            // find a variable whose name start on index 0
            for (int endIndex = 0; endIndex < expression.length(); endIndex++)
            {
                char letter = expression.charAt(endIndex);
                if (((letter < 'a' || letter > 'z') && (letter < 'A' || letter > 'Z') && (letter < '0' || letter > '9'))
                        || endIndex == expression.length() - 1)
                {
                    if (endIndex == expression.length() - 1)
                        endIndex++;
                    varName = expression.substring(0, endIndex);
                    break;
                }
            }

            if (DefineVarCommand.getSymbolTable().get(varName) == null)
                return expression; // there is no '-' and no variable so no need to do anything

            // there is var and it is negative. for example there is the var "alt = -370",
            // and we want to calculate "alt" we need to convert it to "(0alt)"
            if (DefineVarCommand.getSymbolTable().get(varName).getValue() < 0)
                expression = expression.replace(varName, "(0" + varName + ")");
        }
        return expression;

    }

    private static boolean isEndlishLetter(char c)
    {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private static boolean isDigit(char c)
    {
        return (c >= '0' && c <= '9');
    }
}

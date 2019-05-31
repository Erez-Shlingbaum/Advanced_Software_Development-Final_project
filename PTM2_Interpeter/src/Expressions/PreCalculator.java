package Expressions;

import Commands.DefineVarCommand;

import java.util.LinkedList;
import java.util.List;

public class PreCalculator
{

	// "5 + 3 / 800 - var" -> "5+3/800-var"
	public static String connectWords(String[] expression)
	{
		StringBuilder stringBuilder = new StringBuilder();
		for (String str : expression)
			stringBuilder.append(str);
		return stringBuilder.toString();
	}


	//replace variable names with their value
	public static String replaceVarNames(String expression) throws Exception
	{
		//variable declaration
		int startIndex = 0;
		int endIndex = 0;
		List<String> variableNames = new LinkedList<>();

		//iterate on the expression and find variable names while ignoring Operators or '()'
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
			expression = expression.replace(varName, Integer.toString((int)varValue));
		}
		variableNames.clear();
		return expression;
	}

	private static boolean isEndlishLetter(char c)
	{
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
}

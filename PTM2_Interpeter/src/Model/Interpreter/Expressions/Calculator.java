package Model.Interpreter.Expressions;

import java.util.LinkedList;
import java.util.Stack;

public class Calculator
{
	//Shunting Yard algorithm
	public static double calculate(String expression) throws Exception
	{
		if (!validations(expression))
			System.out.println("throw exception");

		LinkedList<String> queue = new LinkedList<>();
		Stack<String> stack = new Stack<>();
		int len = expression.length();
		String token = "";
		for (int i = 0; i < len; i++)
		{
			// if we have a number
			if (expression.charAt(i) >= '0' && expression.charAt(i) <= '9')
			{
				token = expression.charAt(i) + "";
				// find all digits of this number
				while (i + 1 < len && ((expression.charAt(i + 1) >= '0' && expression.charAt(i + 1) <= '9') || expression.charAt(i + 1) == '.')) // added ||
					token = token + expression.charAt(++i);
			} else
				token = expression.charAt(i) + "";

			switch (token)
			{
				case "+":
					while (!stack.isEmpty() && (stack.peek().equals("/") || stack.peek().equals("*")))
					{
						queue.addFirst(stack.pop());
					}
					stack.push(token);
					break;
				case "-":
					while (!stack.isEmpty() && (stack.peek().equals("/") || stack.peek().equals("*")))
						queue.addFirst(stack.pop());
					stack.push(token);
					break;
				case "*":
					stack.push(token);
					break;
				case "/":
					stack.push(token);
					break;
				case "(":
					stack.push(token);
					break;
				case ")":
					while (!stack.isEmpty() && !(stack.peek().equals("(")))
						queue.addFirst(stack.pop());
					stack.pop();
					break;
				default: // Always a number
					queue.addFirst(token);
					break;
			}
		}
		while (!stack.isEmpty())
			queue.addFirst(stack.pop());
		Expression finalExpression = buildExpression(queue);
		//System.out.println(queue.size());
		return finalExpression.calculate();
	}

	private static boolean validations(String expression)
	{
		return true; // TODO implement validations

	}

	private static Expression buildExpression(LinkedList<String> queue)
	{
		Expression returnedExpression = null;
		Expression right = null;
		Expression left = null;
		String currentExpression = queue.removeFirst();
		if (currentExpression.equals("+") || currentExpression.equals("-") || currentExpression.equals("*")
				|| currentExpression.equals("/"))
		{
			right = buildExpression(queue);
			left = buildExpression(queue);
		}
		switch (currentExpression)
		{
			case "+":
				returnedExpression = new Plus(left, right);
				break;
			case "-":
				returnedExpression = new Minus(left, right);
				break;
			case "*":
				returnedExpression = new Mul(left, right);
				break;
			case "/":
				returnedExpression = new Div(left, right);
				break;
			default:
				returnedExpression = new Number(Double.parseDouble(currentExpression));
				break;
		}

		return returnedExpression;
	}
}

package Commands;

import Interpeter.Variable;
import Operators.AssignmentOperator;

import java.util.*;

public class DefineVarCommand implements Command
{
	//Hash Map shared by all define var objects
	static HashMap<String, Variable> symbolTable = new HashMap<>();

	public static HashMap<String, Variable> getSymbolTable()
	{
		return symbolTable;
	}

	@Override
	public void execute(String[] args) throws Exception
	{
		String varName = null;
		if(String.join("",args).contains("="))
		{
			Scanner scanner = new Scanner(String.join("", args));
			scanner.useDelimiter("=");
			varName = scanner.next().trim();
			scanner.useDelimiter("");
			scanner.next("=");
			List<String> expression = new ArrayList<String>(Arrays.asList(scanner.nextLine().trim()));

			expression.add(0, "=");
			expression.add(0, varName);
			args = expression.toArray(new String[0]);// varName, =, expression

		}
		else
			varName = args[0];

		//check more exceptions
		if (symbolTable.containsKey(varName))
			throw new Exception("Error: trying to define variable that already exists in symbolTable");
		symbolTable.put(varName, new Variable());

		if (args.length >= 2)
			if (args[1].equals("="))
				new AssignmentOperator().execute(args);
			else throw new Exception("Syntax error: var expects '='");
	}
}
//var varName = bind "/path/xxxx/xxx"
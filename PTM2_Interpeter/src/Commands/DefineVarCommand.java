package Commands;

import Interpeter.Variable;
import Operators.AssignmentOperator;

import java.util.HashMap;

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
		System.out.println("Var executed!");    //TODO: delete this

		//variable declaration
		String varName = args[0];
		// String path = args[3];
		// path = path.substring(1, path.length() - 1); //remove first and last "

		//check more exceptions
		if (symbolTable.containsKey(varName))
			throw new Exception("Error: trying to define variable that already exists in symbolTable");
		symbolTable.put(varName, new Variable());

		if (args.length >= 2)//TODO: changed this, notify shaked
			if (args[1].equals("="))
				new AssignmentOperator().execute(args);
			else throw new Exception("Syntax error: var expects '='");
	}
}
//var varName = bind "/path/xxxx/xxx"
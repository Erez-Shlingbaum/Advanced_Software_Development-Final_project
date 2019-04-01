package Commands;

import interpeter.Variable;
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

        //check exception
        if (args.length != 4 || !args[1].equals("=") || !args[2].equals("bind")
                || args[3].charAt(0) != '"' || args[3].charAt(args[3].length() - 1) != '"')
            throw new Exception("Syntax error: 'var' expects - \'name = bind \"path\"\'");

        //variable declaration
        String varName = args[0];
        String path = args[3];
        path = path.substring(1, path.length() - 1); //remove first and last "

        //check more exceptions
        if (symbolTable.containsKey(varName))
            throw new Exception("Error: trying to define variable that already exists in symbolTable");

        symbolTable.put(varName, new Variable(path));
    }//TODO: remember that declaration and defenition can be seperated. TODO: operator= for 'varName = bind..'
}
//var varName = bind "/path/xxxx/xxx"
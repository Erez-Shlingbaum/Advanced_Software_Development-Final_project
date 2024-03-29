package Model.Interpreter.Interpeter;

import Model.Interpreter.Client_Side.Client;

public class Variable
{
    private String path = null;
    private double value;

    private final Client client;

    public Variable(InterpreterContext context)
    {
        this.client = context.client;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }//removeCommas(path); }

    public double getValue()
    {
        return value;
    }

    public void setValue(double value, boolean notifyServer) throws Exception
    {
        if (notifyServer)
            this.client.sendMessage("set " + this.path + " " + value);
        else // If we notify server then we need to respond to the simulator in realtime. if we change our value now then we will respond to an event before it happened!
            this.value = value;
    }

    //check if there are "" in start or end of str and removes them
    /*private static String removeCommas(String str) throws Exception
	{
		if(str.charAt(0) == '"' && str.charAt(str.length()-1) == '"')
			return str.substring(1, str.length()-1);
		throw new Exception("Syntax error: expecting \"path\"");
	}*/
}
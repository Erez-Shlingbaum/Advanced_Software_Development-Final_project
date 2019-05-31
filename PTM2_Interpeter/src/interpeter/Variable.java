package Interpeter;

import Client_Side.ConnectClient;

public class Variable
{
    String path = null;
    double value;

    public String getPath() { return path; }

    public void setPath(String path)  throws Exception { this.path = removeCommas(path); }

    public double getValue() { return value; }

    public void setValue(double value, boolean notifyServer) throws Exception
	{
		this.value = value;
		if(notifyServer)
			ConnectClient.getReference().sendMessage("set " + this.path + " " + this.value);
	}

    //check if there are "" in start or end of str and removes them
    private static String removeCommas(String str) throws Exception
	{
		if(str.charAt(0) == '"' && str.charAt(str.length()-1) == '"')
			return str.substring(1, str.length()-1);
		throw new Exception("Syntax error: expecting \"path\"");
	}
}
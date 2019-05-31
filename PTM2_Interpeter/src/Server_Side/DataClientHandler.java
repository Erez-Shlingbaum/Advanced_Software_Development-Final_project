package Server_Side;

import Commands.DefineVarCommand;

import java.io.*;

public class DataClientHandler implements ClientHandler
{
	int linesPerSecond;

	public DataClientHandler(int linesPerSecond)
	{
		this.linesPerSecond = linesPerSecond;
	}

	//outClient is not used
	@Override
	public void handleClient(InputStream in, OutputStream out) //handle conversation
	{
		BufferedReader clientInput = new BufferedReader(new InputStreamReader(in));
		String str;

		try
		{
			while ((str = clientInput.readLine()) != null )//this reads 10 lines per second, unless the server sends in a different rate // TODO ADD WHILE NOT CLOSE
			{

				//remember to send false
				//DefineVarCommand.getSymbolTable().get("name").setValue(value, false);
				System.out.println("simulator sent to dataServer: " + str);
			}

			clientInput.close();
			out.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}


	}
}

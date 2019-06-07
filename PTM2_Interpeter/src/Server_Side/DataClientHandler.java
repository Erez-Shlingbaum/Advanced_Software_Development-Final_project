package Server_Side;

import Commands.DefineVarCommand;
import Interpeter.Variable;

import java.io.*;
import java.util.Collection;

public class DataClientHandler implements ClientHandler
{
	int linesPerSecond;
	public static volatile boolean isStop = false;

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
			// TODO: REMEMBER THAT READLINE IS A BLOCKING CALL, SO THIS THREAD WILL BE IN BLOCKED AND WILL NOT SEE THE isStop Variable until new input arrvies!. this thread never dies. fix it later if we need to
			while ((str = clientInput.readLine()) != null && !this.isStop)//this reads 10 lines per second, unless the server sends in a different rate // TODO ADD WHILE NOT CLOSE
			{
				String[] simNames = {"simX", "simY", "simZ"};
				String[] simsXYZ = str.split(",");

				Variable[] variables = DefineVarCommand.getSymbolTable().values().toArray(new Variable[0]);

				for (int i = 0; i < simNames.length; i++)
				{
					for (int j = 0; j < variables.length; j++)
						if(simNames[i].equals(variables[j].getPath()))
							try
							{
								variables[j].setValue(Double.parseDouble(simsXYZ[i]), false);
								break;
							} catch (Exception e)
							{
								e.printStackTrace();
							}
				}
				//DefineVarCommand.getSymbolTable().get("name").setValue(value, false)

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

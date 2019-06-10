package Model.Server_Side;

import Model.Commands.DefineVarCommand;
import Model.Interpeter.Variable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
		System.out.println("Data server is running...");
		BufferedReader clientInput = new BufferedReader(new InputStreamReader(in));
		String str;
		List<String> simNames = new ArrayList<>(24);

		try
		{
			// Create a list of simulator bind paths from file (the order of the paths is important)
			Scanner simulatorBinds = new Scanner(new File("./PTM2_Interpeter/Resources/simulator_variables_paths.txt"));
			while(simulatorBinds.hasNextLine())
				simNames.add(simulatorBinds.nextLine());
		} catch (FileNotFoundException e) {e.printStackTrace();}


		try
		{
			// REMEMBER THAT READLINE IS A BLOCKING CALL, SO THIS THREAD WILL BE IN BLOCKED AND WILL NOT SEE THE isStop Variable until new input arrvies!.
			// this thread never dies. fix it later if we need to
			while ((str = clientInput.readLine()) != null && !this.isStop)//this reads 10 lines per second, unless the server sends in a different rate // TODO ADD WHILE NOT CLOSE
			{
				String[] simsXYZ = str.split(",");
				Variable[] variables = DefineVarCommand.getSymbolTable().values().toArray(new Variable[0]);

				for (int i = 0; i < simNames.size(); i++)
					for (int j = 0; j < variables.length; j++)
						if (simNames.get(i).equals(variables[j].getPath()))
							try
							{
								//System.out.println("DataServer: updating var '" + variables[j].getPath() + "' with " + simsXYZ[i]);
								variables[j].setValue(Double.parseDouble(simsXYZ[i]), false);
								break;
							} catch (Exception e) {e.printStackTrace();}
				//DefineVarCommand.getSymbolTable().get("name").setValue(value, false)

				//System.out.println("simulator sent to dataServer: " + str);
			}

			clientInput.close();
			out.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

package Server_Side;

import java.io.*;

public class DataClientHandler implements ClientHandler
{
	//int linesPerSecond;

	/*public DataClientHandler(int linesPerSecond)
	{
		this.linesPerSecond = linesPerSecond;
	}*/

	//outClient is not used
	@Override
	public void handleClient(InputStream in, OutputStream out)//handle conversation
	{
		BufferedReader clientInput = new BufferedReader(new InputStreamReader(in));

		String str;
		try
		{
			while ((str = clientInput.readLine()) != null)
			{
				//TODO: check if the input is really always double
				//double v
				System.out.println("simulator sent to dataServer: " + str);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}

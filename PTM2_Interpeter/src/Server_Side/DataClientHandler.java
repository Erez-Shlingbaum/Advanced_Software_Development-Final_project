package Server_Side;

import java.io.InputStream;
import java.io.OutputStream;

public class DataClientHandler implements ClientHandler
{
	int linesPerSecond;

	public DataClientHandler(int linesPerSecond)
	{
		this.linesPerSecond = linesPerSecond;
	}

	@Override
	public void handleClient(InputStream inputClient, OutputStream outClient)
	{

	}
}

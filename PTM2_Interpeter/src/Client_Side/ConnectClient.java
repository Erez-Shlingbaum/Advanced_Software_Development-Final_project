package Client_Side;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectClient implements Client
{
	Socket socket = null;

	@Override
	public void connect(String ipAddress, int port) throws Exception
	{
		if(socket != null)
			throw new Exception("Syntax error: trying to connect twice");
		try
		{
			socket = new Socket(ipAddress, port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(String command)
	{
		try
		{
			PrintWriter writerToServer = new PrintWriter(socket.getOutputStream());
			writerToServer.println(command);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void close()
	{
		try
		{
			if(!socket.isClosed())
				socket.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

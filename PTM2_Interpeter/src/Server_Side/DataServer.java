package Server_Side;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class DataServer implements Server
{
	static DataServer ref = null;

	public static DataServer getReference()    //singelton pattern
	{
		if (ref == null)
			ref = new DataServer();
		return ref;
	}

	public static boolean isReferenceExists()
	{
		if (ref == null)
			return false;
		return true;
	}

	private DataServer() { }

	@Override
	public void open(int port, ClientHandler clientHandler)
	{
		new Thread(()->runServer(port, clientHandler)).start();
		/*
		another way to write:
		Thread thread = new Thread(
				new Runnable()
				{
					@Override
					public void run()
					{
						runServer(port, clientHandler);
					}
				});
		thread.start();
		*/
	}

	private void runServer(int port, ClientHandler clientHandler)
	{
		try
		{
			//open server
			ServerSocket serverSocket = new ServerSocket(port);
			Socket clientSocket = serverSocket.accept();

			//conversation

			clientHandler.handleClient(clientSocket.getInputStream(), clientSocket.getOutputStream());

			//close server
			clientSocket.close();
			serverSocket.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void close()
	{
		DataClientHandler.isStop = true; // stop the thread from running
	}
}



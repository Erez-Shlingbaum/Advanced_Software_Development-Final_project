package Server_Side;

public class DataServer implements Server
{
	static DataServer ref;

	public static DataServer getReference()	//singelton pattern
	{
		if (ref == null)
			ref = new DataServer();
		return ref;
	}

	//TODO: static method 'isReferenceExists' to check if server already running

	private DataServer() { }

	@Override
	public void open(int port, ClientHandler clientHandler)
	{

	}

	@Override
	public void close()
	{

	}
}

package Model.Interpreter.Server_Side;

public interface Server
{
	void open(int port, ClientHandler clientHandler);
	void close();
}
